package com.rgr.rutappa.android.screen

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.rgr.rutappa.domain.useCase.ResultUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class ResultViewModel(
    private val resultUseCase: ResultUseCase
): ViewModel() {

    private val _state: MutableStateFlow<ResultState> = MutableStateFlow(ResultState.Idle)
    val state = _state.asStateFlow()
    fun calculateResult() {
        viewModelScope.launch {
            _state.update { ResultState.Loading }
            resultUseCase.invoke().onSuccess { success->
                _state.update { ResultState.Loaded(success) }
            }.onFailure {
                _state.update { ResultState.Idle }
            }
        }
    }
}

sealed class ResultState {
    data object Idle: ResultState()
    data object Loading: ResultState()
    data class Loaded(val votes: List<Pair<String, Int>>): ResultState()
}