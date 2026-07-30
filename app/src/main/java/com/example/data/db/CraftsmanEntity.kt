package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "craftsmen")
data class CraftsmanEntity(
    @PrimaryKey val id: String,
    val name: String,
    val categoryKey: String,
    val phone: String,
    val whatsapp: String,
    val wilayaCode: Int,
    val commune: String,
    val ratingScore: Double, // Out of 10.0 (e.g., 9.4)
    val ratingCount: Int,
    val dailyRateDzd: Int, // e.g. 4000 DZD
    val isVerified: Boolean = true,
    val yearsExperience: Int = 5,
    val description: String = "",
    val skillsCsv: String = "", // e.g. "Villa building,Tilework,Plaster"
    val isAvailable: Boolean = true,
    val avatarIndex: Int = 0,
    val isUserCreated: Boolean = false,
    val distanceKmSimulated: Double = 2.5
)
