package com.example.data

import android.content.Context
import android.content.SharedPreferences
import androidx.security.crypto.EncryptedSharedPreferences
import androidx.security.crypto.MasterKey
import com.example.data.db.UserDao
import com.example.data.db.UserEntity
import com.example.data.remote.SupabaseAuthApi
import com.example.data.remote.SupabaseAuthApiProvider
import com.example.data.remote.SupabaseSignInRequest
import com.example.data.remote.SupabaseSignUpRequest
import com.example.data.remote.UpsertProfileBody
import java.io.IOException
import java.util.Locale
import java.util.UUID
import retrofit2.HttpException

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class EmailConfirmationRequired(
        val email: String,
        val userType: String,
        val userId: String
    ) : AuthResult()
    data class Error(val messageKey: String) : AuthResult()
}

class UserRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val legacyPrefs: SharedPreferences = context.getSharedPreferences(
        "herafi_user_session",
        Context.MODE_PRIVATE
    )
    private val prefs: SharedPreferences = EncryptedSharedPreferences.create(
        context,
        "herafi_user_session_secure",
        MasterKey.Builder(context)
            .setKeyScheme(MasterKey.KeyScheme.AES256_GCM)
            .build(),
        EncryptedSharedPreferences.PrefKeyEncryptionScheme.AES256_SIV,
        EncryptedSharedPreferences.PrefValueEncryptionScheme.AES256_GCM
    )
    private val supabaseAuth: SupabaseAuthApi? = SupabaseAuthApiProvider.create()

    init {
        migrateLegacySession()
    }

    private fun migrateLegacySession() {
        if (prefs.getString("current_user_id", null) != null) return
        val userId = legacyPrefs.getString("current_user_id", null) ?: return
        prefs.edit()
            .putString("current_user_id", userId)
            .putString("supabase_access_token", legacyPrefs.getString("supabase_access_token", null))
            .putString("supabase_refresh_token", legacyPrefs.getString("supabase_refresh_token", null))
            .apply()
        legacyPrefs.edit()
            .remove("supabase_access_token")
            .remove("supabase_refresh_token")
            .apply()
    }

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        userType: String = "CLIENT",
        phone: String = "",
        wilayaCode: Int = 16
    ): AuthResult {
        val trimmedName = fullName.trim()
        val trimmedEmail = email.trim().lowercase()
        if (trimmedName.isBlank()) return AuthResult.Error("error_name_empty")
        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            return AuthResult.Error("error_invalid_email")
        }
        if (password.length < 8) return AuthResult.Error("error_password_too_short")

        // Supabase is authoritative when configured. A stale local row from a
        // previous interrupted sign-up must not block a new cloud registration.
        val existing = userDao.getUserByEmail(trimmedEmail)
        val authApi = supabaseAuth
        if (authApi != null) {
            val remote = runCatching {
                authApi.signUp(
                    SupabaseSignUpRequest(
                        email = trimmedEmail,
                        password = password,
                        data = mapOf(
                            "full_name" to trimmedName,
                            "phone" to phone.trim(),
                            "user_type" to userType,
                            "wilaya_code" to wilayaCode.toString()
                        )
                    )
                )
            }.getOrElse { throwable ->
                return AuthResult.Error(classifyAuthError(throwable, isSignIn = false))
            }
            // A successful HTTP response without a user is not proof that the email exists.
            // Keep the result neutral instead of showing a misleading duplicate-email message.
            val remoteUser = remote.user ?: return AuthResult.Error("error_auth_unknown")
            if (remote.accessToken.isNullOrBlank()) {
                // Supabase can return a user without a session when email confirmation
                // is enabled. An empty identities list, however, denotes an existing
                // account and must not be presented as a successful new registration.
                if (remoteUser.identities?.isEmpty() == true) {
                    return AuthResult.Error("error_email_already_registered")
                }
                return AuthResult.EmailConfirmationRequired(trimmedEmail, userType, remoteUser.id)
            }
            val localSalt = PasswordHasher.newSalt()
            val localShadowPassword = UUID.randomUUID().toString()
            val newUser = UserEntity(
                id = remoteUser.id,
                fullName = trimmedName,
                email = trimmedEmail,
                passwordHash = PasswordHasher.hash(localShadowPassword, localSalt),
                passwordSalt = localSalt,
                userType = userType,
                phone = phone.trim(),
                wilayaCode = wilayaCode
            )
            userDao.insertUser(newUser)
            saveSession(newUser.id, remote.accessToken, remote.refreshToken)
            ensureRemoteProfile(newUser)
            return AuthResult.Success(newUser)
        }

        if (existing != null) {
            return AuthResult.Error("error_email_already_registered")
        }

        val salt = PasswordHasher.newSalt()
        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            fullName = trimmedName,
            email = trimmedEmail,
            passwordHash = PasswordHasher.hash(password, salt),
            passwordSalt = salt,
            userType = userType,
            phone = phone.trim(),
            wilayaCode = wilayaCode
        )

        userDao.insertUser(newUser)
        saveSession(newUser.id)
        return AuthResult.Success(newUser)
    }

    suspend fun loginUser(email: String, password: String): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (trimmedEmail.isBlank() || !isValidEmail(trimmedEmail)) {
            return AuthResult.Error("error_invalid_email")
        }
        if (password.isBlank()) return AuthResult.Error("error_password_empty")

        val authApi = supabaseAuth
        if (authApi != null) {
            val remote = runCatching {
                authApi.signIn(SupabaseSignInRequest(trimmedEmail, password))
            }.getOrElse { throwable ->
                return AuthResult.Error(classifySignInError(throwable))
            }
            val remoteUser = remote.user ?: return AuthResult.Error("error_user_not_found")
            val existing = userDao.getUserById(remoteUser.id)
            val user = existing ?: UserEntity(
                id = remoteUser.id,
                fullName = remoteUser.userMetadata?.get("full_name") ?: trimmedEmail.substringBefore("@"),
                email = remoteUser.email ?: trimmedEmail,
                passwordHash = "",
                passwordSalt = "",
                userType = remoteUser.userMetadata?.get("user_type") ?: "CLIENT",
                phone = remoteUser.userMetadata?.get("phone") ?: "",
                wilayaCode = remoteUser.userMetadata?.get("wilaya_code")?.toIntOrNull() ?: 16
            ).also { userDao.insertUser(it) }
            saveSession(user.id, remote.accessToken, remote.refreshToken)
            ensureRemoteProfile(user)
            return AuthResult.Success(user)
        }

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return AuthResult.Error("error_user_not_found")

        if (!PasswordHasher.verify(password, user.passwordSalt, user.passwordHash)) {
            return AuthResult.Error("error_incorrect_password")
        }

        saveSession(user.id)
        return AuthResult.Success(user)
    }

    suspend fun getSavedUser(): UserEntity? {
        val savedUserId = prefs.getString("current_user_id", null) ?: return null
        return userDao.getUserById(savedUserId)
    }

    fun getSupabaseAccessToken(): String? = prefs.getString("supabase_access_token", null)

    fun getCurrentUserId(): String? = prefs.getString("current_user_id", null)

    /**
     * Converts transport/API failures into stable UI keys without exposing raw
     * server responses, URLs, tokens, or implementation details to the user.
     */
    private fun classifySignInError(throwable: Throwable): String =
        classifyAuthError(throwable, isSignIn = true)

    private fun classifyAuthError(throwable: Throwable, isSignIn: Boolean): String {
        if (throwable is IOException) return "error_auth_network"

        if (throwable is HttpException) {
            val statusCode = throwable.code()
            val serverBody = runCatching {
                throwable.response()?.errorBody()?.string().orEmpty()
            }.getOrDefault("").lowercase(Locale.ROOT)

            if (serverBody.contains("email not confirmed") ||
                serverBody.contains("email_not_confirmed")
            ) {
                return "error_email_confirmation_required"
            }

            if (!isSignIn && (
                    statusCode == 400 || statusCode == 422
                ) && (
                    serverBody.contains("already registered") ||
                    serverBody.contains("already exists") ||
                    serverBody.contains("user_already_exists")
                )
            ) {
                return "error_email_already_registered"
            }

            if (isSignIn && statusCode == 400 && (
                    serverBody.contains("invalid login credentials") ||
                    serverBody.contains("invalid_credentials")
                )
            ) {
                return "error_incorrect_password"
            }

            return when (statusCode) {
                400, 401 -> if (isSignIn) "error_incorrect_password" else "error_auth_unknown"
                429 -> if (isSignIn) "error_auth_rate_limited" else "error_signup_rate_limited"
                in 500..599 -> "error_auth_server"
                else -> "error_auth_unknown"
            }
        }

        return "error_auth_unknown"
    }

    private suspend fun ensureRemoteProfile(user: UserEntity) {
        val api = com.example.data.remote.SupabaseApiProvider.create(getSupabaseAccessToken()) ?: return
        runCatching {
            api.upsertProfile(
                UpsertProfileBody(
                    id = user.id,
                    display_name = user.fullName,
                    phone = user.phone.ifBlank { null },
                    role = if (user.userType.equals("CRAFTSMAN", ignoreCase = true)) "craftsman" else "customer"
                )
            )
        }
    }

    fun logoutUser() {
        prefs.edit()
            .remove("current_user_id")
            .remove("supabase_access_token")
            .remove("supabase_refresh_token")
            .apply()
    }

    private fun saveSession(userId: String, accessToken: String? = null, refreshToken: String? = null) {
        prefs.edit()
            .putString("current_user_id", userId)
            .apply {
                if (!accessToken.isNullOrBlank()) putString("supabase_access_token", accessToken)
                if (!refreshToken.isNullOrBlank()) putString("supabase_refresh_token", refreshToken)
            }
            .apply()
    }

    private fun isValidEmail(email: String): Boolean {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}
