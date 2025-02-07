package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.PartnerState
import com.rgr.rutappa.domain.useCase.DeleteAccountUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PartnerViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<PartnerState> = MutableStateFlow(PartnerState())
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PartnerState()
    ).toCommonStateFlow()

    fun deleteAccount() {
        scope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            val result = deleteAccountUseCase.invoke()
            if(result.isSuccess) {
                _state.update {
                    it.copy(isLoading = false, logout = true)
                }
            } else {
                _state.update {
                    it.copy(isLoading = false, error = true)
                }
            }
        }
    }
}