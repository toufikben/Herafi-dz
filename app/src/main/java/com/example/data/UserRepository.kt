package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.UserDao
import com.example.data.db.UserEntity
import com.example.data.remote.SupabaseAuthApi
import com.example.data.remote.SupabaseAuthApiProvider
import com.example.data.remote.SupabaseSignInRequest
import com.example.data.remote.SupabaseSignUpRequest
import com.example.data.remote.UpsertProfileBody
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val messageKey: String) : AuthResult()
}

class UserRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences(
        "herafi_user_session",
        Context.MODE_PRIVATE
    )
    private val supabaseAuth: SupabaseAuthApi? = SupabaseAuthApiProvider.create()

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

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return AuthResult.Error("error_email_already_registered")
        }

        if (supabaseAuth != null) {
            val remote = runCatching {
                supabaseAuth.signUp(
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
            }.getOrElse { return AuthResult.Error("error_auth_network") }
            val remoteUser = remote.user ?: return AuthResult.Error("error_email_already_registered")
            if (remote.accessToken.isNullOrBlank()) {
                return AuthResult.Error("error_email_confirmation_required")
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

        if (supabaseAuth != null) {
            val remote = runCatching {
                supabaseAuth.signIn(SupabaseSignInRequest(trimmedEmail, password))
            }.getOrElse { return AuthResult.Error("error_incorrect_password") }
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
