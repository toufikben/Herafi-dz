package com.example.data

import android.content.Context
import android.content.SharedPreferences
import com.example.data.db.UserDao
import com.example.data.db.UserEntity
import java.util.UUID

sealed class AuthResult {
    data class Success(val user: UserEntity) : AuthResult()
    data class Error(val messageKey: String) : AuthResult()
}

class UserRepository(
    private val userDao: UserDao,
    context: Context
) {
    private val prefs: SharedPreferences = context.getSharedPreferences("herafi_user_session", Context.MODE_PRIVATE)

    suspend fun registerUser(
        fullName: String,
        email: String,
        password: String,
        userType: String = "CLIENT",
        phone: String = "",
        wilayaCode: Int = 16
    ): AuthResult {
        val trimmedEmail = email.trim().lowercase()
        if (fullName.isBlank()) return AuthResult.Error("error_name_empty")
        if (trimmedEmail.isBlank() || !trimmedEmail.contains("@")) return AuthResult.Error("error_invalid_email")
        if (password.length < 6) return AuthResult.Error("error_password_too_short")

        val existing = userDao.getUserByEmail(trimmedEmail)
        if (existing != null) {
            return AuthResult.Error("error_email_already_registered")
        }

        val newUser = UserEntity(
            id = UUID.randomUUID().toString(),
            fullName = fullName.trim(),
            email = trimmedEmail,
            password = password,
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
        if (trimmedEmail.isBlank()) return AuthResult.Error("error_invalid_email")
        if (password.isBlank()) return AuthResult.Error("error_password_empty")

        val user = userDao.getUserByEmail(trimmedEmail)
            ?: return AuthResult.Error("error_user_not_found")

        if (user.password != password) {
            return AuthResult.Error("error_incorrect_password")
        }

        saveSession(user.id)
        return AuthResult.Success(user)
    }

    suspend fun getSavedUser(): UserEntity? {
        val savedUserId = prefs.getString("current_user_id", null) ?: return null
        return userDao.getUserById(savedUserId)
    }

    fun logoutUser() {
        prefs.edit().remove("current_user_id").apply()
    }

    private fun saveSession(userId: String) {
        prefs.edit().putString("current_user_id", userId).apply()
    }
}
