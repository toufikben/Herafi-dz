package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CraftsmanDao {
    @Query("SELECT * FROM craftsmen ORDER BY ratingScore DESC LIMIT :limit")
    fun getAllCraftsmen(limit: Int = Int.MAX_VALUE): Flow<List<CraftsmanEntity>>

    @Query("SELECT COUNT(*) FROM craftsmen")
    fun countCraftsmen(): kotlinx.coroutines.flow.Flow<Int>

    @Query("SELECT * FROM craftsmen WHERE id = :id")
    fun getCraftsmanById(id: String): Flow<CraftsmanEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCraftsman(craftsman: CraftsmanEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCraftsmen(craftsmen: List<CraftsmanEntity>)

    @Query("SELECT * FROM craftsmen WHERE ownerId = :ownerId LIMIT 1")
    suspend fun getCraftsmanByOwnerId(ownerId: String): CraftsmanEntity?

    @Query("DELETE FROM craftsmen WHERE ownerId = :ownerId AND id != :keepId")
    suspend fun deleteOtherCraftsmanRowsForOwner(ownerId: String, keepId: String)

    @Query("UPDATE craftsmen SET ratingScore = :newScore, ratingCount = :newCount WHERE id = :id")
    suspend fun updateCraftsmanRating(id: String, newScore: Double, newCount: Int)

    // Reviews
    @Query("SELECT * FROM reviews WHERE craftsmanId = :craftsmanId ORDER BY timestamp DESC")
    fun getReviewsForCraftsman(craftsmanId: String): Flow<List<ReviewEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertReview(review: ReviewEntity)

    // Bookmarks
    @Query("SELECT craftsmanId FROM bookmarks")
    fun getBookmarkIds(): Flow<List<String>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addBookmark(bookmark: BookmarkEntity)

    @Query("DELETE FROM bookmarks WHERE craftsmanId = :craftsmanId")
    suspend fun removeBookmark(craftsmanId: String)
}
