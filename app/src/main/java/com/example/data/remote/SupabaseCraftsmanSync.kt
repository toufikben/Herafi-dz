package com.example.data.remote

import com.example.data.db.CraftsmanDao
import com.example.data.db.CraftsmanEntity

sealed interface SyncResult {
    data class Success(val importedCount: Int) : SyncResult
    data object NotConfigured : SyncResult
    data class Failed(val message: String) : SyncResult
}

class SupabaseCraftsmanSync(
    private val dao: CraftsmanDao,
    private val api: SupabaseApi?
) {
    suspend fun refreshPublishedCraftsmen(): SyncResult {
        if (api == null) return SyncResult.NotConfigured
        return runCatching {
            val remote = api.getPublishedCraftsmen()
            dao.insertCraftsmen(remote.map { it.toLocalEntity() })
            SyncResult.Success(remote.size)
        }.getOrElse { error ->
            SyncResult.Failed(error.message ?: "Supabase request failed")
        }
    }
}

private fun RemoteCraftsman.toLocalEntity(): CraftsmanEntity {
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
        isAvailable = status == "published",
        avatarIndex = kotlin.math.abs(id.hashCode()) % 12,
        isUserCreated = false,
        distanceKmSimulated = 0.0
    )
}
