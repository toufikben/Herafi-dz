package com.example.data.db

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "service_requests",
    indices = [Index(value = ["customerId", "clientRequestId"], unique = true)]
)
data class ServiceRequestEntity(
    @PrimaryKey     val id: String,
    val clientRequestId: String = id,
    val remoteId: String? = null,
    val customerId: String,
    val craftsmanId: String? = null,
    val categoryKey: String,
    val wilayaCode: String,
    val commune: String,
    val description: String,
    val imageUrls: String = "[]",
    val status: String = STATUS_DRAFT,
    val syncState: String = SYNC_PENDING,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val customerDisplayName: String? = null,
    val customerPhone: String? = null,
    val craftsmanName: String? = null,
    val craftsmanPhone: String? = null,
    val craftsmanRating: Double = 0.0,
    val craftsmanCategory: String? = null,
    val craftsmanWilaya: String? = null,
    val isMine: Boolean = false
) {
    fun imageUrlsList(): List<String> = runCatching {
        val moshi = com.squareup.moshi.Moshi.Builder()
            .addLast(com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory())
            .build()
        val type = com.squareup.moshi.Types.newParameterizedType(List::class.java, String::class.java)
        moshi.adapter<List<String>>(type).fromJson(imageUrls) ?: emptyList()
    }.getOrDefault(emptyList())

    companion object {
        const val STATUS_DRAFT = "draft"
        const val STATUS_OPEN = "open"
        const val STATUS_QUOTED = "quoted"
        const val STATUS_ACCEPTED = "accepted"
        const val STATUS_IN_PROGRESS = "in_progress"
        const val STATUS_COMPLETED = "completed"
        const val STATUS_CANCELLED = "cancelled"
        const val SYNC_PENDING = "pending"
        const val SYNCED = "synced"
        const val SYNC_FAILED = "failed"
    }
}
