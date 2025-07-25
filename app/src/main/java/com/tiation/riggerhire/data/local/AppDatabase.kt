package com.tiation.riggerhire.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.tiation.riggerhire.data.local.converter.DateTimeConverters
import com.tiation.riggerhire.data.local.dao.ActivityLogDao
import com.tiation.riggerhire.data.local.dao.RollHistoryDao
import com.tiation.riggerhire.data.local.entity.ActivityLog
import com.tiation.riggerhire.data.local.entity.RollHistory

@Database(
    entities = [
        RollHistory::class,
        ActivityLog::class
    ],
    version = 1,
    exportSchema = true
)
@TypeConverters(DateTimeConverters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun rollHistoryDao(): RollHistoryDao
    abstract fun activityLogDao(): ActivityLogDao

    companion object {
        private const val DATABASE_NAME = "rigger_hire_db"

        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getInstance(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    DATABASE_NAME
                )
                .fallbackToDestructiveMigration()
                .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
