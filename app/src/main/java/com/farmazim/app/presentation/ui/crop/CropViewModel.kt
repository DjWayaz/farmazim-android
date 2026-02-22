package com.farmazim.app.presentation.ui.crop

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmazim.app.data.billing.PremiumManager
import com.farmazim.app.domain.model.CropRecord
import com.farmazim.app.domain.model.Plot
import com.farmazim.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CropUiState(
    val plots: List<Plot> = emptyList(),
    val crops: List<CropRecord> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null,
    val isPremium: Boolean = false,
    val plotCount: Int = 0
)

@HiltViewModel
class CropViewModel @Inject constructor(
    private val getAllPlotsUseCase: GetAllPlotsUseCase,
    private val getPlotCountUseCase: GetPlotCountUseCase,
    private val addPlotUseCase: AddPlotUseCase,
    private val deletePlotUseCase: DeletePlotUseCase,
    private val getAllCropsUseCase: GetAllCropsUseCase,
    private val addCropUseCase: AddCropUseCase,
    private val deleteCropUseCase: DeleteCropUseCase,
    private val premiumManager: PremiumManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(CropUiState())
    val uiState: StateFlow<CropUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(
                getAllPlotsUseCase(),
                getAllCropsUseCase(),
                premiumManager.isPremium
            ) { plots, crops, isPremium ->
                CropUiState(plots = plots, crops = crops, isPremium = isPremium, plotCount = plots.size)
            }.collect { _uiState.value = it }
        }
    }

    fun canAddPlot(): Boolean {
        return _uiState.value.isPremium || _uiState.value.plotCount < PremiumManager.FREE_PLOT_LIMIT
    }

    fun addPlot(name: String, sizeHectares: Double) {
        viewModelScope.launch {
            addPlotUseCase(Plot(name = name, sizeHectares = sizeHectares))
                .onFailure { _uiState.update { it.copy(error = "Failed to save plot.") } }
        }
    }

    fun deletePlot(plot: Plot) {
        viewModelScope.launch {
            deletePlotUseCase(plot)
                .onFailure { _uiState.update { it.copy(error = "Failed to delete plot.") } }
        }
    }

    fun addCrop(crop: CropRecord) {
        viewModelScope.launch {
            addCropUseCase(crop)
                .onFailure { _uiState.update { it.copy(error = "Failed to save crop.") } }
        }
    }

    fun deleteCrop(crop: CropRecord) {
        viewModelScope.launch {
            deleteCropUseCase(crop)
                .onFailure { _uiState.update { it.copy(error = "Failed to delete crop.") } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
