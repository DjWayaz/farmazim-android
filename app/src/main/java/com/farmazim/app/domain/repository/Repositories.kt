package com.farmazim.app.domain.repository

import com.farmazim.app.domain.model.*
import kotlinx.coroutines.flow.Flow

interface PlotRepository {
    fun getAllPlots(): Flow<List<Plot>>
    suspend fun getPlotCount(): Int
    suspend fun addPlot(plot: Plot): Long
    suspend fun updatePlot(plot: Plot)
    suspend fun deletePlot(plot: Plot)
}

interface CropRepository {
    fun getAllCrops(): Flow<List<CropRecord>>
    fun getCropsByPlot(plotId: Long): Flow<List<CropRecord>>
    suspend fun addCrop(crop: CropRecord): Long
    suspend fun updateCrop(crop: CropRecord)
    suspend fun deleteCrop(crop: CropRecord)
    suspend fun getTotalIncome(): Double
}

interface InputRepository {
    fun getAllInputs(): Flow<List<InputRecord>>
    fun getInputsByPlot(plotId: Long): Flow<List<InputRecord>>
    suspend fun addInput(input: InputRecord): Long
    suspend fun updateInput(input: InputRecord)
    suspend fun deleteInput(input: InputRecord)
    suspend fun getTotalExpenses(): Double
}

interface LivestockRepository {
    fun getAllLivestock(): Flow<List<LivestockGroup>>
    suspend fun addLivestock(group: LivestockGroup): Long
    suspend fun updateLivestock(group: LivestockGroup)
    suspend fun deleteLivestock(group: LivestockGroup)
    fun getEventsByGroup(groupId: Long): Flow<List<LivestockEvent>>
    suspend fun addEvent(event: LivestockEvent): Long
    suspend fun deleteEvent(event: LivestockEvent)
}

interface FinanceRepository {
    suspend fun getFinanceSummary(): FinanceSummary
}
