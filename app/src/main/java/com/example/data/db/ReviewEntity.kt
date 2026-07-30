package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "reviews")
data class ReviewEntity(
    @PrimaryKey val id: String,
    val craftsmanId: String,
    val reviewerName: String,
    val scoreTen: Double, // 1.0 to 10.0 score
    val comment: String,
    val timestamp: Long = System.currentTimeMillis(),
    val qualityFinishScore: Double = 9.0,
    val punctualityScore: Double = 9.0,
    val priceFairnessScore: Double = 9.0,
    val tagsCsv: String = "Punctual,Quality Finish"
)
