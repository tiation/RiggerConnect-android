package com.tiation.riggerhire.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "roll_history")
data class RollHistory(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val rollId: String,
    val userId: String,
    val equipmentId: String?,
    val timestamp: LocalDateTime,
    val description: String,
    val status: String,
    val weight: Double?,
    val location: String?,
    val notes: String?
)
