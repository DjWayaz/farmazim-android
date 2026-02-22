package com.farmazim.app.presentation.ui.input

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.farmazim.app.domain.model.InputRecord
import com.farmazim.app.domain.model.Plot
import com.farmazim.app.domain.usecase.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import javax.inject.Inject

data class InputUiState(
    val inputs: List<InputRecord> = emptyList(),
    val plots: List<Plot> = emptyList(),
    val isLoading: Boolean = false,
    val error: String? = null
)

@HiltViewModel
class InputViewModel @Inject constructor(
    private val getAllInputsUseCase: GetAllInputsUseCase,
    private val getAllPlotsUseCase: GetAllPlotsUseCase,
    private val addInputUseCase: AddInputUseCase,
    private val deleteInputUseCase: DeleteInputUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InputUiState())
    val uiState: StateFlow<InputUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            combine(getAllInputsUseCase(), getAllPlotsUseCase()) { inputs, plots ->
                InputUiState(inputs = inputs, plots = plots)
            }.collect { _uiState.value = it }
        }
    }

    fun addInput(input: InputRecord) {
        viewModelScope.launch {
            addInputUseCase(input).onFailure { _uiState.update { it.copy(error = "Failed to save input.") } }
        }
    }

    fun deleteInput(input: InputRecord) {
        viewModelScope.launch {
            deleteInputUseCase(input).onFailure { _uiState.update { it.copy(error = "Failed to delete input.") } }
        }
    }

    fun clearError() = _uiState.update { it.copy(error = null) }
}
