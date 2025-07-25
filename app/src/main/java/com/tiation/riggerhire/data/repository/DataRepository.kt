package com.tiation.riggerhire.data.repository

import com.tiation.riggerhire.data.local.dao.ActivityLogDao
import com.tiation.riggerhire.data.local.dao.RollHistoryDao
import com.tiation.riggerhire.data.local.entity.ActivityLog
import com.tiation.riggerhire.data.local.entity.RollHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DataRepository @Inject constructor(
    private val rollHistoryDao: RollHistoryDao,
    private val activityLogDao: ActivityLogDao
) {
    // Roll History Operations
    fun getAllRollHistory(): Flow<List<RollHistory>> = rollHistoryDao.getAllRollHistory()
    
    fun getRollHistoryByUser(userId: String): Flow<List<RollHistory>> = 
        rollHistoryDao.getRollHistoryByUser(userId)
    
    fun getRollHistoryByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<RollHistory>> =
        rollHistoryDao.getRollHistoryByDateRange(startDate, endDate)
    
    suspend fun insertRollHistory(rollHistory: RollHistory) {
        rollHistoryDao.insertRollHistory(rollHistory)
        // Log the activity
        logActivity(
            ActivityLog(
                userId = rollHistory.userId,
                timestamp = LocalDateTime.now(),
                activityType = "ROLL_HISTORY_CREATED",
                description = "New roll history entry created",
                relatedEntityId = rollHistory.rollId,
                relatedEntityType = "ROLL_HISTORY"
            )
        )
    }
    
    suspend fun updateRollHistory(rollHistory: RollHistory) {
        rollHistoryDao.updateRollHistory(rollHistory)
        // Log the update
        logActivity(
            ActivityLog(
                userId = rollHistory.userId,
                timestamp = LocalDateTime.now(),
                activityType = "ROLL_HISTORY_UPDATED",
                description = "Roll history entry updated",
                relatedEntityId = rollHistory.rollId,
                relatedEntityType = "ROLL_HISTORY"
            )
        )
    }
    
    suspend fun deleteRollHistory(rollHistory: RollHistory) {
        rollHistoryDao.deleteRollHistory(rollHistory)
        // Log the deletion
        logActivity(
            ActivityLog(
                userId = rollHistory.userId,
                timestamp = LocalDateTime.now(),
                activityType = "ROLL_HISTORY_DELETED",
                description = "Roll history entry deleted",
                relatedEntityId = rollHistory.rollId,
                relatedEntityType = "ROLL_HISTORY"
            )
        )
    }

    // Activity Log Operations
    fun getAllActivityLogs(): Flow<List<ActivityLog>> = activityLogDao.getAllActivityLogs()
    
    fun getActivityLogsByUser(userId: String): Flow<List<ActivityLog>> =
        activityLogDao.getActivityLogsByUser(userId)
    
    fun getActivityLogsByType(type: String): Flow<List<ActivityLog>> =
        activityLogDao.getActivityLogsByType(type)
    
    fun getActivityLogsByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<ActivityLog>> =
        activityLogDao.getActivityLogsByDateRange(startDate, endDate)
    
    suspend fun logActivity(activityLog: ActivityLog) {
        activityLogDao.insertActivityLog(activityLog)
    }
    
    suspend fun deleteOldData(olderThan: LocalDateTime) {
        rollHistoryDao.deleteOldRollHistory(olderThan)
        activityLogDao.deleteOldActivityLogs(olderThan)
    }
    
    fun getActivityLogsByEntity(entityId: String, entityType: String): Flow<List<ActivityLog>> =
        activityLogDao.getActivityLogsByEntity(entityId, entityType)
}
