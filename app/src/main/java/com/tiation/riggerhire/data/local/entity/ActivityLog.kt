package com.tiation.riggerhire.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.time.LocalDateTime

@Entity(tableName = "activity_logs")
data class ActivityLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val userId: String,
    val timestamp: LocalDateTime,
    val activityType: String,
    val description: String,
    val relatedEntityId: String?,
    val relatedEntityType: String?,
    val metadata: String? // JSON string for additional data
)
