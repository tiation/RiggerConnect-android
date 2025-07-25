package com.tiation.riggerhire.data.local.dao

import androidx.room.*
import com.tiation.riggerhire.data.local.entity.RollHistory
import kotlinx.coroutines.flow.Flow
import java.time.LocalDateTime

@Dao
interface RollHistoryDao {
    @Query("SELECT * FROM roll_history ORDER BY timestamp DESC")
    fun getAllRollHistory(): Flow<List<RollHistory>>

    @Query("SELECT * FROM roll_history WHERE userId = :userId ORDER BY timestamp DESC")
    fun getRollHistoryByUser(userId: String): Flow<List<RollHistory>>

    @Query("SELECT * FROM roll_history WHERE timestamp BETWEEN :startDate AND :endDate ORDER BY timestamp DESC")
    fun getRollHistoryByDateRange(startDate: LocalDateTime, endDate: LocalDateTime): Flow<List<RollHistory>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRollHistory(rollHistory: RollHistory)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRollHistories(rollHistories: List<RollHistory>)

    @Update
    suspend fun updateRollHistory(rollHistory: RollHistory)

    @Delete
    suspend fun deleteRollHistory(rollHistory: RollHistory)

    @Query("DELETE FROM roll_history WHERE timestamp < :olderThan")
    suspend fun deleteOldRollHistory(olderThan: LocalDateTime)

    @Query("SELECT * FROM roll_history WHERE rollId = :rollId")
    suspend fun getRollHistoryById(rollId: String): RollHistory?
}
