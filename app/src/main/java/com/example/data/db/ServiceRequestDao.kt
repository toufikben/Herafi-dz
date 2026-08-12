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

    @Query("SELECT * FROM service_requests WHERE id = :id LIMIT 1")
    suspend fun findById(id: String): ServiceRequestEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(request: ServiceRequestEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(requests: List<ServiceRequestEntity>)

    @Query("UPDATE service_requests SET remoteId = :remoteId, status = :status, syncState = :syncState, updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markSynced(localId: String, remoteId: String, status: String, syncState: String, updatedAt: Long)

    @Query("UPDATE service_requests SET syncState = :syncState, updatedAt = :updatedAt WHERE id = :localId")
    suspend fun markSyncState(localId: String, syncState: String, updatedAt: Long)
}
