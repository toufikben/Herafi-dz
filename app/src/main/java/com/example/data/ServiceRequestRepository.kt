package com.example.data

import android.content.Context
import com.example.data.db.ServiceRequestDao
import com.example.data.db.ServiceRequestEntity
import com.example.data.remote.CreateServiceRequestBody
import com.example.data.remote.RemoteServiceRequest
import com.example.data.remote.SupabaseApiProvider
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
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

    suspend fun getAssignedRequests(): List<ServiceRequestEntity> {
        // Fetch all requests assigned to any craftsman profile the current owner has.
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
        val profiles = api?.let {
            runCatching {
                it.getCraftsmanForOwner(
                    ownerId = "eq.${userRepository.getCurrentUserId().orEmpty()}",
                    select = "id",
                    limit = 10
                )
            }.getOrDefault(emptyList())
        }.orEmpty()
        val ownedIds = profiles.map { it.id }
        if (ownedIds.isEmpty()) return emptyList()
        var merged = emptyList<ServiceRequestEntity>()
        ownedIds.forEach { cid ->
            var items: List<ServiceRequestEntity>? = null
            dao.getForCraftsman(cid).collect { items = it }
            merged = mergeRequests(merged, items ?: emptyList())
        }
        return merged.sortedByDescending { it.createdAt }
    }

    suspend fun updateRequestStatusForCraftsman(
        remoteRequestId: String,
        newStatus: String
    ): Result<Unit> {
        val userId = userRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("auth_required"))
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return Result.failure(IllegalStateException("not_configured"))
        return runCatching {
            val updated = api.updateServiceRequestStatus(
                requestId = "eq.$remoteRequestId",
                body = com.example.data.remote.UpdateServiceRequestStatusBody(newStatus)
            ).firstOrNull() ?: error("empty_response")
            // Reflect the new status locally immediately, before the next polling tick.
            dao.findById("remote_$remoteRequestId")?.let { local ->
                dao.markSyncState(local.id, ServiceRequestEntity.SYNCED, System.currentTimeMillis())
                dao.setStatus(remoteRequestId, newStatus)
            }
            Unit
        }
    }

    suspend fun refreshForCraftsmanDirect(): Result<Int> {
        val ownerId = userRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("auth_required"))
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return Result.failure(IllegalStateException("not_configured"))
        return runCatching {
            val remoteRequests = api.getServiceRequestsForCraftsman(ownerFilter = "eq.$ownerId")
            dao.deleteRemoteRequests()
            dao.insertAll(remoteRequests.map { remote ->
                remote.toCraftsmanEntity(remote.craftsman_name)
            })
            // Requests assigned to any craftsman profile this user owns are managed
            // by this user, so surface them as own requests in the local list.
            remoteRequests.forEach { remote ->
                dao.markAsMine(remote.id)
            }
            remoteRequests.size
        }
    }

    private suspend fun mergeRequests(
        existing: List<ServiceRequestEntity>,
        incoming: List<ServiceRequestEntity>
    ): List<ServiceRequestEntity> {
        val map = existing.associateBy { it.remoteId ?: it.id }
        val merged = ArrayList<ServiceRequestEntity>(existing)
        incoming.forEach { incomingRequest ->
            val key = incomingRequest.remoteId ?: incomingRequest.id
            if (map.containsKey(key)) {
                merged.replaceAll { if ((it.remoteId ?: it.id) == key) incomingRequest else it }
            } else {
                merged.add(incomingRequest)
            }
        }
        return merged
    }

    private suspend fun refreshForCraftsman(
        ownerId: String,
        api: com.example.data.remote.SupabaseApi
    ): Result<Int> {
        return runCatching {
            // Find the craftsman profile this owner published (RLS allows owners to see theirs).
            val profile = api.getCraftsmanForOwner(
                ownerId = "eq.$ownerId",
                select = "id",
                limit = 1
            ).firstOrNull() ?: return@runCatching 0
            val remoteRequests = api.getServiceRequestsForCraftsman(ownerFilter = "eq.$ownerId")
            dao.deleteRemoteRequests()
            dao.insertAll(remoteRequests.map { remote ->
                remote.toCraftsmanEntity(remote.craftsman_name)
            })
            remoteRequests.size
        }
    }

    private fun com.example.data.remote.RemoteServiceRequest.toCraftsmanEntity(
        craftsmanName: String?
    ): ServiceRequestEntity {
        // Extract the embedded craftsman details from the JSON join payload.
        val craft = craftsmanPayload
        return ServiceRequestEntity(
            id = "remote_$id",
            clientRequestId = client_request_id ?: id,
            remoteId = id,
            customerId = customer_id,
            craftsmanId = craftsman_id,
            categoryKey = category_key,
            wilayaCode = wilaya_code,
            commune = commune,
            description = description,
            imageUrls = stringListToJson(image_urls),
            status = status,
            syncState = ServiceRequestEntity.SYNCED,
            createdAt = parseTimestamp(created_at) ?: System.currentTimeMillis(),
            updatedAt = parseTimestamp(updated_at) ?: System.currentTimeMillis(),
            craftsmanName = craft?.name ?: craftsmanName,
            craftsmanPhone = craft?.phone,
            craftsmanRating = craft?.rating_score ?: 0.0,
            craftsmanCategory = craft?.category_key,
            craftsmanWilaya = craft?.wilaya_code,
            isMine = false
        )
    }

    private fun com.example.data.remote.RemoteServiceRequest.toEntity(
        customerId: String,
        dao: ServiceRequestDao,
        customerDisplayName: String? = null
    ): ServiceRequestEntity {
        val existing = dao.findByRemoteId(id)
        return ServiceRequestEntity(
            id = existing?.id ?: "remote_$id",
            clientRequestId = existing?.clientRequestId ?: client_request_id ?: id,
            remoteId = id,
            customerId = customerId,
            craftsmanId = craftsman_id,
            categoryKey = category_key,
            wilayaCode = wilaya_code,
            commune = commune,
            description = description,
            imageUrls = stringListToJson(image_urls),
            status = status,
            syncState = ServiceRequestEntity.SYNCED,
            createdAt = parseTimestamp(created_at) ?: existing?.createdAt ?: System.currentTimeMillis(),
            updatedAt = parseTimestamp(updated_at) ?: existing?.updatedAt ?: System.currentTimeMillis(),
            customerDisplayName = customerDisplayName,
            isMine = true
        )
    }

    private suspend fun fetchCustomerDisplayNames(
        api: com.example.data.remote.SupabaseApi,
        customerIds: List<String>
    ): Map<String, String> {
        if (customerIds.isEmpty()) return emptyMap()
        return runCatching {
            api.getProfiles(
                id = "in.(${customerIds.joinToString(",") { "\"$it\"" }})",
                select = "id,display_name"
            ).associate { it.id to (it.display_name ?: it.id) }
        }.getOrDefault(emptyMap())
    }

    suspend fun refreshCurrentUserRequests(): Result<Int> {
        val customerId = userRepository.getCurrentUserId()
            ?: return Result.failure(IllegalStateException("auth_required"))
        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return Result.failure(IllegalStateException("not_configured"))

        return runCatching {
            val remoteRequests = api.getServiceRequestsForCustomer(customerId)
            val customerNames = fetchCustomerDisplayNames(api, remoteRequests.mapNotNull { it.customer_id.takeIf { it.isNotBlank() } }.distinct())
            dao.insertAll(remoteRequests.map { remote -> remote.toEntity(customerId, dao, customerNames[remote.customer_id]) })
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
                        client_request_id = localRequest.clientRequestId,
                        customer_id = customerId,
                        craftsman_id = localRequest.craftsmanId.toRemoteCraftsmanId(),
                        category_key = localRequest.categoryKey,
                        wilaya_code = localRequest.wilayaCode,
                        commune = localRequest.commune,
                        description = localRequest.description,
                        status = localRequest.status,
                        image_urls = runCatching {
                            val list: List<String> = moshiStringList().fromJson(localRequest.imageUrls) ?: emptyList()
                            list.take(MAX_PHOTOS)
                        }.getOrNull()
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
        description: String,
        imageUrls: List<String> = emptyList()
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
            clientRequestId = localId,
            customerId = customerId,
            craftsmanId = craftsmanId,
            categoryKey = categoryKey.trim().take(80),
            wilayaCode = wilayaCode.trim().take(10),
            commune = commune.trim().take(100),
            description = cleanDescription,
            imageUrls = moshiStringList().toJson(imageUrls.take(MAX_PHOTOS)),
            status = ServiceRequestEntity.STATUS_OPEN,
            syncState = ServiceRequestEntity.SYNC_PENDING
        )
        dao.insert(localRequest)

        val api = SupabaseApiProvider.create(userRepository.getSupabaseAccessToken())
            ?: return ServiceRequestResult.SavedOffline(localRequest)
        return runCatching {
            val remote = api.createServiceRequest(
                    CreateServiceRequestBody(
                    client_request_id = localRequest.clientRequestId,
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

    companion object {
        const val MAX_PHOTOS = 3

        private fun moshiStringList(): com.squareup.moshi.JsonAdapter<List<String>> {
            val moshi = Moshi.Builder().addLast(KotlinJsonAdapterFactory()).build()
            return moshi.adapter<List<String>>(Types.newParameterizedType(List::class.java, String::class.java))
        }

        private fun stringListToJson(value: List<String>?): String =
            runCatching { moshiStringList().toJson(value ?: emptyList()) }.getOrDefault("[]")
    }

    private fun String?.toRemoteCraftsmanId(): String? =
        this?.let { value ->
            when {
                value.startsWith("remote_") -> value.removePrefix("remote_")
                runCatching { UUID.fromString(value) }.isSuccess -> value
                else -> null
            }
        }

    private fun parseTimestamp(value: String?): Long? = value?.let {
        runCatching {
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX", Locale.US).parse(it)?.time
        }.getOrNull()
    }
}
