package com.tiation.riggerhire.data.local.dao

import androidx.room.*
import com.tiation.riggerhire.data.local.entity.ActivityLog
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface ActivityLogDao {
    @Query("SELECT * FROM activity_logs ORDER BY timestamp DESC")
    fun getAllActivityLogs(): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE userId = :userId ORDER BY timestamp DESC")
    fun getActivityLogsByUser(userId: String): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE activityType = :type ORDER BY timestamp DESC")
    fun getActivityLogsByType(type: String): Flow<List<ActivityLog>>

    @Query("SELECT * FROM activity_logs WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getActivityLogsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ActivityLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLog(activityLog: ActivityLog)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertActivityLogs(activityLogs: List<ActivityLog>)

    @Update
    suspend fun updateActivityLog(activityLog: ActivityLog)

    @Delete
    suspend fun deleteActivityLog(activityLog: ActivityLog)

    @Query("DELETE FROM activity_logs WHERE timestamp < :olderThan")
    suspend fun deleteOldActivityLogs(olderThan: LocalDateTime)

    @Query("SELECT * FROM activity_logs WHERE relatedEntityId = :entityId AND relatedEntityType = :entityType")
    fun getActivityLogsByEntity(entityId: String, entityType: String): Flow<List<ActivityLog>>
}
