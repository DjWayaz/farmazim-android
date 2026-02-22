package com.farmazim.app.data.local.dao

import androidx.room.*
import com.farmazim.app.data.local.entity.*
import kotlinx.coroutines.flow.Flow

@Dao
interface PlotDao {
    @Query("SELECT * FROM plots ORDER BY createdAt DESC")
    fun getAllPlots(): Flow<List<PlotEntity>>

    @Query("SELECT COUNT(*) FROM plots")
    suspend fun getPlotCount(): Int

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertPlot(plot: PlotEntity): Long

    @Update
    suspend fun updatePlot(plot: PlotEntity)

    @Delete
    suspend fun deletePlot(plot: PlotEntity)

    @Query("SELECT * FROM plots WHERE id = :id")
    suspend fun getPlotById(id: Long): PlotEntity?
}

@Dao
interface CropRecordDao {
    @Query("SELECT * FROM crop_records WHERE plotId = :plotId ORDER BY plantedAt DESC")
    fun getCropsByPlot(plotId: Long): Flow<List<CropRecordEntity>>

    @Query("SELECT * FROM crop_records ORDER BY plantedAt DESC")
    fun getAllCrops(): Flow<List<CropRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCrop(crop: CropRecordEntity): Long

    @Update
    suspend fun updateCrop(crop: CropRecordEntity)

    @Delete
    suspend fun deleteCrop(crop: CropRecordEntity)

    @Query("SELECT SUM(saleAmountUsd) FROM crop_records WHERE saleAmountUsd IS NOT NULL")
    suspend fun getTotalIncome(): Double?
}

@Dao
interface InputRecordDao {
    @Query("SELECT * FROM input_records WHERE plotId = :plotId ORDER BY appliedAt DESC")
    fun getInputsByPlot(plotId: Long): Flow<List<InputRecordEntity>>

    @Query("SELECT * FROM input_records ORDER BY appliedAt DESC")
    fun getAllInputs(): Flow<List<InputRecordEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertInput(input: InputRecordEntity): Long

    @Update
    suspend fun updateInput(input: InputRecordEntity)

    @Delete
    suspend fun deleteInput(input: InputRecordEntity)

    @Query("SELECT SUM(costUsd) FROM input_records")
    suspend fun getTotalExpenses(): Double?
}

@Dao
interface LivestockGroupDao {
    @Query("SELECT * FROM livestock_groups ORDER BY createdAt DESC")
    fun getAllLivestock(): Flow<List<LivestockGroupEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLivestock(group: LivestockGroupEntity): Long

    @Update
    suspend fun updateLivestock(group: LivestockGroupEntity)

    @Delete
    suspend fun deleteLivestock(group: LivestockGroupEntity)

    @Query("SELECT * FROM livestock_groups WHERE id = :id")
    suspend fun getLivestockById(id: Long): LivestockGroupEntity?
}

@Dao
interface LivestockEventDao {
    @Query("SELECT * FROM livestock_events WHERE livestockGroupId = :groupId ORDER BY date DESC")
    fun getEventsByGroup(groupId: Long): Flow<List<LivestockEventEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEvent(event: LivestockEventEntity): Long

    @Delete
    suspend fun deleteEvent(event: LivestockEventEntity)
}
