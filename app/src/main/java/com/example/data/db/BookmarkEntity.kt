package com.example.data.db

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarks")
data class BookmarkEntity(
    @PrimaryKey val craftsmanId: String,
    val addedAt: Long = System.currentTimeMillis()
)
