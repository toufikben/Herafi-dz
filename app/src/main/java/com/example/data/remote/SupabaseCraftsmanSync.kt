package com.example.data.remote

import com.example.data.db.CraftsmanDao
import com.example.data.db.CraftsmanEntity
import com.example.data.db.ReviewEntity
import retrofit2.HttpException
import java.io.IOException

sealed interface SyncResult {
    data class Success(val importedCount: Int) : SyncResult
    data object NotConfigured : SyncResult
    data class Failed(
        val message: String,
        val httpCode: Int? = null
    ) : SyncResult
}

class SupabaseCraftsmanSync(
    private val dao: CraftsmanDao,
    private val api: SupabaseApi?
) {
    suspend fun refreshPublishedCraftsmen(): SyncResult {
        val supabaseApi = api ?: return SyncResult.NotConfigured
        return runCatching {
            val remote = supabaseApi.getPublishedCraftsmen()
            dao.insertCraftsmen(remote.map { it.toLocalEntity() })
            SyncResult.Success(remote.size)
        }.getOrElse { error ->
            error.toSyncFailure("Supabase request failed")
        }
    }

    suspend fun upsertOwnedCraftsman(ownerId: String, local: CraftsmanEntity): SyncResult {
        val supabaseApi = api ?: return SyncResult.NotConfigured
        return runCatching {
            // PostgREST cannot use ON CONFLICT / merge-duplicates against the partial
            // unique index on owner_id, so resolve by fetching first, then PATCH or POST.
            val existing = supabaseApi.getCraftsmanForOwner(ownerId = "eq.$ownerId").firstOrNull()
            val body = UpsertCraftsmanBody(
                owner_id = ownerId,
                name = local.name.trim().take(80),
                category_key = local.categoryKey.trim().ifBlank { "BUILDER" },
                wilaya_code = local.wilayaCode.coerceIn(0, 58).toString(),
                commune = local.commune.trim().take(80),
                phone = local.phone.trim().take(24),
                whatsapp = local.whatsapp.trim().take(24).ifBlank { null },
                description = local.description.trim().take(1_000),
                daily_rate_dzd = local.dailyRateDzd.coerceIn(0, 10_000_000),
                years_experience = local.yearsExperience.coerceIn(0, 80),
                skills_csv = local.skillsCsv.trim().take(500),
                status = if (local.isAvailable) "published" else "pending",
                is_available = local.isAvailable
            )
            val remote = if (existing != null) {
                supabaseApi.updateOwnedCraftsman(ownerId = "eq.$ownerId", body = body).firstOrNull()
                    ?: throw IllegalStateException("Supabase returned no craftsman profile")
            } else {
                val response = supabaseApi.upsertOwnedCraftsman(profile = body)
                response.firstOrNull()
                    ?: throw IllegalStateException("Supabase returned no craftsman profile")
            }
            val restored = remote.toLocalEntity(ownerIdOverride = ownerId, userCreated = true)
            dao.insertCraftsman(restored)
            dao.deleteOtherCraftsmanRowsForOwner(ownerId, restored.id)
            SyncResult.Success(1)
        }.getOrElse { error ->
            error.toSyncFailure("Supabase craftsman upsert failed")
        }
    }

    suspend fun restoreOwnedCraftsman(ownerId: String): SyncResult {
        val supabaseApi = api ?: return SyncResult.NotConfigured
        return runCatching {
            val remote = supabaseApi.getCraftsmanForOwner(ownerId = "eq.$ownerId").firstOrNull()
            if (remote == null) return@runCatching SyncResult.Success(0)
            val restored = remote.toLocalEntity(ownerIdOverride = ownerId, userCreated = true)
            dao.insertCraftsman(restored)
            dao.deleteOtherCraftsmanRowsForOwner(ownerId, restored.id)
            SyncResult.Success(1)
        }.getOrElse { error ->
            error.toSyncFailure("Supabase craftsman restore failed")
        }
    }

    suspend fun refreshReviewsForCraftsman(craftsmanId: String): SyncResult {
        val supabaseApi = api ?: return SyncResult.NotConfigured
        val remoteId = craftsmanId.removePrefix("remote_")
        return runCatching {
            val reviews = supabaseApi.getReviewsForCraftsman(remoteId)
            reviews.forEach { review ->
                dao.insertReview(
                    ReviewEntity(
                        id = "remote_${review.id}",
                        craftsmanId = "remote_${review.craftsman_id}",
                        reviewerName = "مستخدم Herafi DZ",
                        scoreTen = review.score_ten.coerceIn(0.0, 10.0),
                        comment = review.comment.trim().take(500),
                        timestamp = System.currentTimeMillis()
                    )
                )
            }
            SyncResult.Success(reviews.size)
        }.getOrElse { error ->
            error.toSyncFailure("Supabase reviews request failed")
        }
    }
}

private fun Throwable.toSyncFailure(defaultMessage: String): SyncResult.Failed {
    val httpException = this as? HttpException
    val code = httpException?.code()
    val serverMessage = runCatching { httpException?.response()?.errorBody()?.string() }.getOrNull()
        ?.trim()
        ?.takeIf { it.isNotBlank() }
    val message = when {
        code != null && !serverMessage.isNullOrBlank() -> "HTTP $code: $serverMessage"
        code != null -> "HTTP $code: ${httpException.message()}"
        this is IOException -> "Network error: ${this.message ?: defaultMessage}"
        !this.message.isNullOrBlank() -> this.message!!
        else -> defaultMessage
    }
    return SyncResult.Failed(message = message.take(500), httpCode = code)
}

private fun RemoteCraftsman.toLocalEntity(
    ownerIdOverride: String? = owner_id,
    userCreated: Boolean = false
): CraftsmanEntity {
    val localId = "remote_$id"
    return CraftsmanEntity(
        id = localId,
        name = name.trim().take(80),
        categoryKey = category_key.trim().ifBlank { "BUILDER" },
        phone = phone.trim().take(24),
        whatsapp = (whatsapp ?: phone).trim().take(24),
        wilayaCode = wilaya_code.toIntOrNull()?.coerceIn(1, 58) ?: 0,
        commune = commune.trim().take(80),
        ratingScore = rating_score.coerceIn(0.0, 10.0),
        ratingCount = rating_count.coerceAtLeast(0),
        dailyRateDzd = (daily_rate_dzd ?: 0).coerceIn(0, 10_000_000),
        isVerified = is_verified,
        yearsExperience = years_experience.coerceIn(0, 80),
        description = description.trim().take(1_000),
        skillsCsv = skills_csv.trim().take(500),
        isAvailable = is_available,
        avatarIndex = kotlin.math.abs(id.hashCode()) % 12,
        isUserCreated = userCreated,
        distanceKmSimulated = 0.0,
        ownerId = ownerIdOverride
    )
}
