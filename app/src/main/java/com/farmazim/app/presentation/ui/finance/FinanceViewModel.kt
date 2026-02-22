package com.farmazim.app.presentation.ui.finance

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmazim.app.data.billing.PremiumManager
import com.farmazim.app.domain.model.FinanceSummary
import com.farmazim.app.domain.usecase.GetFinanceSummaryUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class FinanceUiState(
    val summary: FinanceSummary? = null,
    val isPremium: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)

@HiltViewModel
class FinanceViewModel @Inject constructor(
    private val getFinanceSummaryUseCase: GetFinanceSummaryUseCase,
    private val premiumManager: PremiumManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(FinanceUiState())
    val uiState: StateFlow<FinanceUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            premiumManager.isPremium.collect { isPremium ->
                _uiState.update { it.copy(isPremium = isPremium) }
                if (isPremium) loadSummary()
            }
        }
    }

    private fun loadSummary() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }
            getFinanceSummaryUseCase()
                .onSuccess { summary -> _uiState.update { it.copy(summary = summary, isLoading = false) } }
                .onFailure { _uiState.update { it.copy(error = "Failed to load summary.", isLoading = false) } }
        }
    }
}
