package com.example.data

import android.content.Context
import com.example.data.db.ServiceRequestDao
import com.example.data.db.ServiceRequestEntity
import com.example.data.remote.CreateServiceRequestBody
import com.example.data.remote.RemoteServiceRequest
import com.example.data.remote.SupabaseApiProvider
import kotlinx.coroutines.flow.Flow
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.UUID

sealed interface ServiceRequestResult {
    data class Success(val request: ServiceRequestEntity) : ServiceRequestResult
    data class SavedOffline(val request: ServiceRequestEntity) : ServiceRequestResult
    data class Error(val message: String) : ServiceRequestResult
}

class ServiceRequestRepository(
    private val dao: ServiceRequestDao,
    private val userRepository: UserRepository,
    context: Context
) {
    private val appContext = context.applicationContext

    fun getForCurrentUser(): Flow<List<ServiceRequestEntity>> {
        val userId = userRepository.getCurrentUserId().orEmpty()
        return dao.getForCustomer(userId)
    }

    suspend fun refreshCurrentUserRequests(): Result<Int> {
        val customerId = userRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("auth_required"))
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return Result.failure(IllegalStateException("not_configured"))

        return runCatching {
            val remoteRequests = api.getServiceRequestsForCustomer(customerId)
            dao.insertAll(remoteRequests.map { remote -> remote.toEntity(customerId, dao) })
            remoteRequests.size
        }
    }

    suspend fun syncPendingRequests(): Int {
        val customerId = userRepository.getCurrentUserId() ?: return 0
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken()) ?: return 0
        var syncedCount = 0
        dao.getPendingForCustomer(customerId).forEach { localRequest ->
            runCatching {
                val remote = api.createServiceRequest(
                    CreateServiceRequestBody(
                        customer_id = customerId,
                        craftsman_id = localRequest.craftsmanId.toRemoteCraftsmanId(),
                        category_key = localRequest.categoryKey,
                        wilaya_code = localRequest.wilayaCode,
                        commune = localRequest.commune,
                        description = localRequest.description,
                        status = localRequest.status
                    )
                ).firstOrNull() ?: error("empty_response")
                dao.markSynced(
                    localId = localRequest.id,
                    remoteId = remote.id,
                    status = remote.status,
                    syncState = ServiceRequestEntity.SYNCED,
                    updatedAt = System.currentTimeMillis()
                )
                syncedCount++
            }.onFailure {
                dao.markSyncState(localRequest.id, ServiceRequestEntity.SYNC_FAILED, System.currentTimeMillis())
            }
        }
        return syncedCount
    }

    suspend fun createRequest(
        craftsmanId: String?,
        categoryKey: String,
        wilayaCode: String,
        commune: String,
        description: String
    ): ServiceRequestResult {
        val customerId = userRepository.getCurrentUserId()
            ?: return ServiceRequestResult.Error("auth_required")
        val cleanDescription = description.trim()
        if (cleanDescription.length !in 10..2000) {
            return ServiceRequestResult.Error("description_invalid")
        }

        val localId = UUID.randomUUID().toString()
        val localRequest = ServiceRequestEntity(
            id = localId,
            customerId = customerId,
            craftsmanId = craftsmanId?.removePrefix("remote_"),
            categoryKey = categoryKey.trim().take(80),
            wilayaCode = wilayaCode.trim().take(10),
            commune = commune.trim().take(100),
            description = cleanDescription,
            status = ServiceRequestEntity.STATUS_OPEN,
            syncState = ServiceRequestEntity.SYNC_PENDING
        )
        dao.insert(localRequest)

        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return ServiceRequestResult.SavedOffline(localRequest)
        return runCatching {
            val remote = api.createServiceRequest(
                CreateServiceRequestBody(
                    customer_id = customerId,
                    craftsman_id = localRequest.craftsmanId.toRemoteCraftsmanId(),
                    category_key = localRequest.categoryKey,
                    wilaya_code = localRequest.wilayaCode,
                    commune = localRequest.commune,
                    description = localRequest.description
                )
            ).firstOrNull() ?: error("empty_response")
            dao.markSynced(
                localId = localId,
                remoteId = remote.id,
                status = remote.status,
                syncState = ServiceRequestEntity.SYNCED,
                updatedAt = System.currentTimeMillis()
            )
            ServiceRequestResult.Success(localRequest.copy(
                remoteId = remote.id,
                status = remote.status,
                syncState = ServiceRequestEntity.SYNCED
            ))
        }.getOrElse {
            dao.markSyncState(localId, ServiceRequestEntity.SYNC_FAILED, System.currentTimeMillis())
            ServiceRequestResult.SavedOffline(localRequest.copy(syncState = ServiceRequestEntity.SYNC_FAILED))
        }
    }

    private suspend fun RemoteServiceRequest.toEntity(
        customerId: String,
        dao: ServiceRequestDao
    ): ServiceRequestEntity {
        val existing = dao.findByRemoteId(id)
        return ServiceRequestEntity(
            id = existing?.id ?: "remote_$id",
            remoteId = id,
            customerId = customerId,
            craftsmanId = craftsman_id,
            categoryKey = category_key,
            wilayaCode = wilaya_code,
            commune = commune,
            description = description,
            status = status,
            syncState = ServiceRequestEntity.SYNCED,
            createdAt = parseTimestamp(created_at) ?: existing?.createdAt ?: System.currentTimeMillis(),
            updatedAt = parseTimestamp(updated_at) ?: existing?.updatedAt ?: System.currentTimeMillis()
        )
    }

    private fun String?.toRemoteCraftsmanId(): String? =
        this?.takeIf { it.startsWith("remote_") }?.removePrefix("remote_")

    private fun parseTimestamp(value: String?): Long? = value?.let {
        runCatching {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).parse(it)?.time
        }.getOrNull()
    }
}
