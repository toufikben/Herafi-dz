package com.example.data.db

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface ServiceRequestDao {
    @Query("SELECT * FROM service_requests WHERE customerId = :customerId ORDER BY createdAt DESC")
    fun getForCustomer(customerId: String): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE craftsmanId = :craftsmanId ORDER BY createdAt DESC")
    fun getForCraftsman(craftsmanId: String): Flow<List<ServiceRequestEntity>>

    @Query("SELECT * FROM service_requests WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ServiceRequestEntity?

    @Query("SELECT * FROM service_requests WHERE remoteId = :remoteId LIMIT 1")
    fun findByRemoteId(remoteId: String): ServiceRequestEntity?

    @Query("SELECT * FROM service_requests WHERE customerId = :customerId AND syncState IN ('pending', 'failed') ORDER BY createdAt ASC")
    suspend fun getPendingForCustomer(customerId: String): List<ServiceRequestEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: ServiceRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<ServiceRequestEntity>)

    @Query("UPDATE service_requests SET remoteId = :remoteId, status = :status, syncState = :syncState, updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markSynced(localId: String, remoteId: String, status: String, syncState: String, updatedAt: Long)

    @Query("UPDATE service_requests SET syncState = :syncState, updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markSyncState(localId: String, syncState: String, updatedAt: Long)

    @Query("UPDATE service_requests SET isMine = 1 WHERE remoteId = :remoteId")
    suspend fun markAsMine(remoteId: String)

    @Query("UPDATE service_requests SET status = :status, updatedAt = :now WHERE remoteId = :remoteId")
    suspend fun setStatus(remoteId: String, status: String, now: Long = System.currentTimeMillis())

    @Query("DELETE FROM service_requests WHERE remoteId IS NOT NULL")
    suspend fun deleteRemoteRequests(): Int
}
