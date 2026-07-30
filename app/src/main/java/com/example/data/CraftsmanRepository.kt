package com.example.data

import com.example.data.db.BookmarkEntity
import com.example.data.db.CraftsmanDao
import com.example.data.db.CraftsmanEntity
import com.example.data.db.ReviewEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.util.UUID

class CraftsmanRepository(private val dao: CraftsmanDao) {

    init {
        // Seed initial data if DB is empty
        CoroutineScope(Dispatchers.IO).launch {
            val existing = dao.getAllCraftsmen().first()
            if (existing.isEmpty()) {
                dao.insertCraftsmen(SeedData.initialCraftsmen)
                for (review in SeedData.sampleReviews) {
                    dao.insertReview(review)
                }
            }
        }
    }

    fun getAllCraftsmen(): Flow<List<CraftsmanEntity>> = dao.getAllCraftsmen()

    fun getCraftsmanById(id: String): Flow<CraftsmanEntity?> = dao.getCraftsmanById(id)

    fun getReviewsForCraftsman(craftsmanId: String): Flow<List<ReviewEntity>> =
        dao.getReviewsForCraftsman(craftsmanId)

    fun getBookmarkIds(): Flow<List<String>> = dao.getBookmarkIds()

    suspend fun toggleBookmark(craftsmanId: String, currentBookmarks: List<String>) {
        if (currentBookmarks.contains(craftsmanId)) {
            dao.removeBookmark(craftsmanId)
        } else {
            dao.addBookmark(BookmarkEntity(craftsmanId = craftsmanId))
        }
    }

    suspend fun submitReview(
        craftsmanId: String,
        reviewerName: String,
        scoreTen: Double,
        comment: String,
        qualityScore: Double,
        punctualityScore: Double,
        priceScore: Double,
        tagsCsv: String,
        currentCraftsman: CraftsmanEntity?
    ) {
        val review = ReviewEntity(
            id = UUID.randomUUID().toString(),
            craftsmanId = craftsmanId,
            reviewerName = if (reviewerName.isBlank()) "مستعمل التطبيق" else reviewerName,
            scoreTen = scoreTen,
            comment = comment,
            qualityFinishScore = qualityScore,
            punctualityScore = punctualityScore,
            priceFairnessScore = priceScore,
            tagsCsv = tagsCsv
        )
        dao.insertReview(review)

        // Calculate new average rating
        if (currentCraftsman != null) {
            val oldCount = currentCraftsman.ratingCount
            val oldScore = currentCraftsman.ratingScore
            val newCount = oldCount + 1
            val newAverage = ((oldScore * oldCount) + scoreTen) / newCount
            val roundedAverage = Math.round(newAverage * 10.0) / 10.0
            dao.updateCraftsmanRating(craftsmanId, roundedAverage, newCount)
        }
    }

    suspend fun registerNewCraftsman(
        name: String,
        categoryKey: String,
        phone: String,
        whatsapp: String,
        wilayaCode: Int,
        commune: String,
        dailyRateDzd: Int,
        yearsExperience: Int,
        description: String,
        skillsCsv: String
    ) {
        val newWorker = CraftsmanEntity(
            id = "worker_${UUID.randomUUID()}",
            name = name,
            categoryKey = categoryKey,
            phone = phone,
            whatsapp = whatsapp.ifBlank { phone },
            wilayaCode = wilayaCode,
            commune = commune.ifBlank { "المركز" },
            ratingScore = 0.0, // Initial score (Unrated / New worker)
            ratingCount = 0,
            dailyRateDzd = dailyRateDzd,
            isVerified = true,
            yearsExperience = yearsExperience,
            description = description,
            skillsCsv = skillsCsv,
            avatarIndex = (0..11).random(),
            isUserCreated = true,
            distanceKmSimulated = 1.0
        )
        dao.insertCraftsman(newWorker)
    }
}
