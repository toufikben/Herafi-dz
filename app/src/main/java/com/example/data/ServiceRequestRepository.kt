package com.example.data

import android.content.Context
import com.example.data.db.ServiceRequestDao
import com.example.data.db.ServiceRequestEntity
import com.example.data.remote.CreateServiceRequestBody
import com.example.data.remote.SupabaseApiProvider
import kotlinx.coroutines.flow.Flow
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
                    craftsman_id = localRequest.craftsmanId,
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
        }.getOrElse { error ->
            dao.markSyncState(localId, ServiceRequestEntity.SYNC_FAILED, System.currentTimeMillis())
            ServiceRequestResult.SavedOffline(localRequest.copy(syncState = ServiceRequestEntity.SYNC_FAILED))
        }
    }
}
