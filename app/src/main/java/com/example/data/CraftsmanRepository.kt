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

    suspend fun getCraftsmanByOwnerId(ownerId: String): CraftsmanEntity? =
        dao.getCraftsmanByOwnerId(ownerId)

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
        val normalizedComment = comment.trim().take(500)
        val normalizedReviewer = reviewerName.trim().take(80).ifBlank { "مستعمل التطبيق" }
        val normalizedScore = scoreTen.coerceIn(0.0, 10.0)
        val review = ReviewEntity(
            id = UUID.randomUUID().toString(),
            craftsmanId = craftsmanId,
            reviewerName = normalizedReviewer,
            scoreTen = normalizedScore,
            comment = normalizedComment,
            qualityFinishScore = qualityScore.coerceIn(0.0, 10.0),
            punctualityScore = punctualityScore.coerceIn(0.0, 10.0),
            priceFairnessScore = priceScore.coerceIn(0.0, 10.0),
            tagsCsv = tagsCsv.trim().take(250)
        )
        dao.insertReview(review)

        // Calculate new average rating
        if (currentCraftsman != null) {
            val oldCount = currentCraftsman.ratingCount
            val oldScore = currentCraftsman.ratingScore
            val newCount = oldCount + 1
            val newAverage = ((oldScore * oldCount) + normalizedScore) / newCount
            val roundedAverage = Math.round(newAverage * 10.0) / 10.0
            dao.updateCraftsmanRating(craftsmanId, roundedAverage, newCount)
        }
    }

    suspend fun registerNewCraftsman(
        ownerId: String? = null,
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
        val normalizedName = name.trim().take(80)
        val normalizedPhone = phone.trim().take(24)
        require(normalizedName.isNotBlank()) { "Craftsman name cannot be blank" }
        require(normalizedPhone.isNotBlank()) { "Craftsman phone cannot be blank" }
        require(wilayaCode in 1..58) { "Invalid Wilaya code" }

        val newWorker = CraftsmanEntity(
            id = "worker_${UUID.randomUUID()}",
            name = normalizedName,
            categoryKey = categoryKey.trim().ifBlank { "BUILDER" },
            phone = normalizedPhone,
            whatsapp = whatsapp.trim().take(24).ifBlank { normalizedPhone },
            wilayaCode = wilayaCode,
            commune = commune.trim().take(80).ifBlank { "المركز" },
            ratingScore = 0.0,
            ratingCount = 0,
            dailyRateDzd = dailyRateDzd.coerceIn(0, 10_000_000),
            isVerified = false,
            yearsExperience = yearsExperience.coerceIn(0, 80),
            description = description.trim().take(1_000),
            skillsCsv = skillsCsv.trim().take(500),
            avatarIndex = (0..11).random(),
            isUserCreated = true,
            distanceKmSimulated = 1.0,
            ownerId = ownerId
        )
        dao.insertCraftsman(newWorker)
    }
}
