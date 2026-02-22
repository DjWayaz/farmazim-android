package com.farmazim.app.data.repository

import com.farmazim.app.data.local.dao.*
import com.farmazim.app.data.local.entity.*
import com.farmazim.app.domain.model.*
import com.farmazim.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

// --- Mappers ---

fun PlotEntity.toDomain() = Plot(id, name, sizeHectares, gpsLat, gpsLng, createdAt)
fun Plot.toEntity() = PlotEntity(id, name, sizeHectares, gpsLat, gpsLng, createdAt)

fun CropRecordEntity.toDomain() = CropRecord(id, plotId, cropType, plantedAt, harvestedAt, yieldKg, saleAmountUsd, notes)
fun CropRecord.toEntity() = CropRecordEntity(id, plotId, cropType, plantedAt, harvestedAt, yieldKg, saleAmountUsd, notes)

fun InputRecordEntity.toDomain() = InputRecord(
    id, plotId,
    inputType = InputType.valueOf(inputType),
    productName, quantityKg, costUsd, appliedAt, notes
)
fun InputRecord.toEntity() = InputRecordEntity(id, plotId, inputType.name, productName, quantityKg, costUsd, appliedAt, notes)

fun LivestockGroupEntity.toDomain() = LivestockGroup(id, species, count, acquiredAt, notes)
fun LivestockGroup.toEntity() = LivestockGroupEntity(id, species, count, acquiredAt, notes)

fun LivestockEventEntity.toDomain() = LivestockEvent(
    id, livestockGroupId,
    eventType = LivestockEventType.valueOf(eventType),
    date, notes, costUsd
)
fun LivestockEvent.toEntity() = LivestockEventEntity(id, livestockGroupId, eventType.name, date, notes, costUsd)

// --- Repository Implementations ---

class PlotRepositoryImpl @Inject constructor(private val dao: PlotDao) : PlotRepository {
    override fun getAllPlots(): Flow<List<Plot>> = dao.getAllPlots().map { it.map { e -> e.toDomain() } }
    override suspend fun getPlotCount(): Int = dao.getPlotCount()
    override suspend fun addPlot(plot: Plot): Long = dao.insertPlot(plot.toEntity())
    override suspend fun updatePlot(plot: Plot) = dao.updatePlot(plot.toEntity())
    override suspend fun deletePlot(plot: Plot) = dao.deletePlot(plot.toEntity())
}

class CropRepositoryImpl @Inject constructor(private val dao: CropRecordDao) : CropRepository {
    override fun getAllCrops(): Flow<List<CropRecord>> = dao.getAllCrops().map { it.map { e -> e.toDomain() } }
    override fun getCropsByPlot(plotId: Long): Flow<List<CropRecord>> = dao.getCropsByPlot(plotId).map { it.map { e -> e.toDomain() } }
    override suspend fun addCrop(crop: CropRecord): Long = dao.insertCrop(crop.toEntity())
    override suspend fun updateCrop(crop: CropRecord) = dao.updateCrop(crop.toEntity())
    override suspend fun deleteCrop(crop: CropRecord) = dao.deleteCrop(crop.toEntity())
    override suspend fun getTotalIncome(): Double = dao.getTotalIncome() ?: 0.0
}

class InputRepositoryImpl @Inject constructor(private val dao: InputRecordDao) : InputRepository {
    override fun getAllInputs(): Flow<List<InputRecord>> = dao.getAllInputs().map { it.map { e -> e.toDomain() } }
    override fun getInputsByPlot(plotId: Long): Flow<List<InputRecord>> = dao.getInputsByPlot(plotId).map { it.map { e -> e.toDomain() } }
    override suspend fun addInput(input: InputRecord): Long = dao.insertInput(input.toEntity())
    override suspend fun updateInput(input: InputRecord) = dao.updateInput(input.toEntity())
    override suspend fun deleteInput(input: InputRecord) = dao.deleteInput(input.toEntity())
    override suspend fun getTotalExpenses(): Double = dao.getTotalExpenses() ?: 0.0
}

class LivestockRepositoryImpl @Inject constructor(
    private val groupDao: LivestockGroupDao,
    private val eventDao: LivestockEventDao
) : LivestockRepository {
    override fun getAllLivestock(): Flow<List<LivestockGroup>> = groupDao.getAllLivestock().map { it.map { e -> e.toDomain() } }
    override suspend fun addLivestock(group: LivestockGroup): Long = groupDao.insertLivestock(group.toEntity())
    override suspend fun updateLivestock(group: LivestockGroup) = groupDao.updateLivestock(group.toEntity())
    override suspend fun deleteLivestock(group: LivestockGroup) = groupDao.deleteLivestock(group.toEntity())
    override fun getEventsByGroup(groupId: Long): Flow<List<LivestockEvent>> = eventDao.getEventsByGroup(groupId).map { it.map { e -> e.toDomain() } }
    override suspend fun addEvent(event: LivestockEvent): Long = eventDao.insertEvent(event.toEntity())
    override suspend fun deleteEvent(event: LivestockEvent) = eventDao.deleteEvent(event.toEntity())
}

class FinanceRepositoryImpl @Inject constructor(
    private val cropDao: CropRecordDao,
    private val inputDao: InputRecordDao
) : FinanceRepository {
    override suspend fun getFinanceSummary(): FinanceSummary {
        val income = cropDao.getTotalIncome() ?: 0.0
        val expenses = inputDao.getTotalExpenses() ?: 0.0
        return FinanceSummary(income, expenses)
    }
}
