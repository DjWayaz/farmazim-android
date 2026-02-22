package com.farmazim.app.domain.usecase

import com.farmazim.app.domain.model.*
import com.farmazim.app.domain.repository.*
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

// Plot use cases
class GetAllPlotsUseCase @Inject constructor(private val repo: PlotRepository) {
    operator fun invoke(): Flow<List<Plot>> = repo.getAllPlots()
}

class GetPlotCountUseCase @Inject constructor(private val repo: PlotRepository) {
    suspend operator fun invoke(): Int = repo.getPlotCount()
}

class AddPlotUseCase @Inject constructor(private val repo: PlotRepository) {
    suspend operator fun invoke(plot: Plot): Result<Long> = runCatching { repo.addPlot(plot) }
}

class DeletePlotUseCase @Inject constructor(private val repo: PlotRepository) {
    suspend operator fun invoke(plot: Plot): Result<Unit> = runCatching { repo.deletePlot(plot) }
}

// Crop use cases
class GetAllCropsUseCase @Inject constructor(private val repo: CropRepository) {
    operator fun invoke(): Flow<List<CropRecord>> = repo.getAllCrops()
}

class AddCropUseCase @Inject constructor(private val repo: CropRepository) {
    suspend operator fun invoke(crop: CropRecord): Result<Long> = runCatching { repo.addCrop(crop) }
}

class UpdateCropUseCase @Inject constructor(private val repo: CropRepository) {
    suspend operator fun invoke(crop: CropRecord): Result<Unit> = runCatching { repo.updateCrop(crop) }
}

class DeleteCropUseCase @Inject constructor(private val repo: CropRepository) {
    suspend operator fun invoke(crop: CropRecord): Result<Unit> = runCatching { repo.deleteCrop(crop) }
}

// Input use cases
class GetAllInputsUseCase @Inject constructor(private val repo: InputRepository) {
    operator fun invoke(): Flow<List<InputRecord>> = repo.getAllInputs()
}

class AddInputUseCase @Inject constructor(private val repo: InputRepository) {
    suspend operator fun invoke(input: InputRecord): Result<Long> = runCatching { repo.addInput(input) }
}

class DeleteInputUseCase @Inject constructor(private val repo: InputRepository) {
    suspend operator fun invoke(input: InputRecord): Result<Unit> = runCatching { repo.deleteInput(input) }
}

// Livestock use cases
class GetAllLivestockUseCase @Inject constructor(private val repo: LivestockRepository) {
    operator fun invoke(): Flow<List<LivestockGroup>> = repo.getAllLivestock()
}

class AddLivestockUseCase @Inject constructor(private val repo: LivestockRepository) {
    suspend operator fun invoke(group: LivestockGroup): Result<Long> = runCatching { repo.addLivestock(group) }
}

class DeleteLivestockUseCase @Inject constructor(private val repo: LivestockRepository) {
    suspend operator fun invoke(group: LivestockGroup): Result<Unit> = runCatching { repo.deleteLivestock(group) }
}

class AddLivestockEventUseCase @Inject constructor(private val repo: LivestockRepository) {
    suspend operator fun invoke(event: LivestockEvent): Result<Long> = runCatching { repo.addEvent(event) }
}

// Finance use cases
class GetFinanceSummaryUseCase @Inject constructor(private val repo: FinanceRepository) {
    suspend operator fun invoke(): Result<FinanceSummary> = runCatching { repo.getFinanceSummary() }
}
