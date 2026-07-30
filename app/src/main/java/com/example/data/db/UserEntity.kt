package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String,
    val fullName: String,
    val email: String,
    val password: String,
    val userType: String = "CLIENT", // "CLIENT" or "CRAFTSMAN"
    val phone: String = "",
    val wilayaCode: Int = 16,
    val createdAt: Long = System.currentTimeMillis()
)

