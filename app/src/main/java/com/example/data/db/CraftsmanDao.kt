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

    /** Paged query used by the UI to avoid materializing the full list on every scroll tick. */
    @Query("SELECT * FROM craftsmen ORDER BY ratingScore DESC LIMIT :limit OFFSET :offset")
    fun getCraftsmenPage(limit: Int, offset: Int): Flow<List<CraftsmanEntity>>

    @Query("SELECT * FROM craftsmen WHERE categoryKey = :categoryKey AND isAvailable = :available ORDER BY ratingScore DESC LIMIT :limit")
    fun getCraftsmenByCategory(categoryKey: String, available: Boolean, limit: Int): Flow<List<CraftsmanEntity>>

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

    @Query("DELETE FROM craftsmen WHERE id = :id")
    suspend fun deleteCraftsmanById(id: String)

    @Query("UPDATE craftsmen SET ratingScore = :newScore, ratingCount = :newCount WHERE id = :id")
    suspend fun updateCraftsmanRating(id: String, newScore: Double, newCount: Int)

    @Query("UPDATE craftsmen SET isAvailable = :available WHERE id = :id")
    suspend fun updateCraftsmanAvailability(id: String, available: Boolean)

    @Query("UPDATE craftsmen SET name = :name, phone = :phone, wilayaCode = :wilayaCode, commune = :commune, dailyRateDzd = :dailyRateDzd, description = :description, isAvailable = :available WHERE id = :id")
    suspend fun updateCraftsmanProfile(id: String, name: String, phone: String, wilayaCode: Int, commune: String, dailyRateDzd: Int, description: String, available: Boolean)

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
