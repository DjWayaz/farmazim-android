package com.farmazim.app.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.farmazim.app.data.local.dao.*
import com.farmazim.app.data.local.entity.*

@Database(
    entities = [
        PlotEntity::class,
        CropRecordEntity::class,
        InputRecordEntity::class,
        LivestockGroupEntity::class,
        LivestockEventEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun plotDao(): PlotDao
    abstract fun cropRecordDao(): CropRecordDao
    abstract fun inputRecordDao(): InputRecordDao
    abstract fun livestockGroupDao(): LivestockGroupDao
    abstract fun livestockEventDao(): LivestockEventDao
}
