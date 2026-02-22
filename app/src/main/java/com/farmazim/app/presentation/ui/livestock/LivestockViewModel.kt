package com.farmazim.app.presentation.ui.livestock

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmazim.app.data.billing.PremiumManager
import com.farmazim.app.domain.model.LivestockGroup
import com.farmazim.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class LivestockUiState(
    val livestock: List<LivestockGroup> = emptyList(),
    val isPremium: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class LivestockViewModel @Inject constructor(
    private val getAllLivestockUseCase: GetAllLivestockUseCase,
    private val addLivestockUseCase: AddLivestockUseCase,
    private val deleteLivestockUseCase: DeleteLivestockUseCase,
    private val premiumManager: PremiumManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(LivestockUiState())
    val uiState: StateFlow<LivestockUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(getAllLivestockUseCase(), premiumManager.isPremium) { livestock, isPremium ->
                LivestockUiState(livestock = livestock, isPremium = isPremium)
            }.collect { _uiState.value = it }
        }
    }

    fun addLivestock(species: String, count: Int, notes: String?) {
        viewModelScope.launch {
            addLivestockUseCase(LivestockGroup(species = species, count = count, acquiredAt = System.currentTimeMillis(), notes = notes))
                .onFailure { _uiState.update { it.copy(error = "Failed to save.") } }
        }
    }

    fun deleteLivestock(group: LivestockGroup) {
        viewModelScope.launch {
            deleteLivestockUseCase(group).onFailure { _uiState.update { it.copy(error = "Failed to delete.") } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
