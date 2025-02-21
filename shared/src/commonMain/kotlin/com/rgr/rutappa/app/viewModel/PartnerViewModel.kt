package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.PartnerState
import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.repository.LocalRepository
import com.rgr.rutappa.domain.useCase.DeleteAccountUseCase
import com.rgr.rutappa.domain.useCase.GetPartnersUseCase
import com.rgr.rutappa.domain.useCase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class PartnerViewModel(
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val localRepository: LocalRepository,
    private val logoutUseCase: LogoutUseCase,
    private val getPartnersUseCase: GetPartnersUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<PartnerState> = MutableStateFlow(PartnerState())
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PartnerState()
    ).toCommonStateFlow()

    fun getPartners(configuration: Int) {
        scope.launch {
            _state.update {
                it.copy(isLoading = true)
            }
            when(val result = getPartnersUseCase.invoke(configuration)) {
                is ResultKMM.Success -> {
                    _state.update {
                        it.copy(isLoading = false, partners = result.data)
                    }
                }
                is ResultKMM.Failure -> {
                    _state.update {
                        it.copy(isLoading = false, error = true)
                    }
                }
            }
        }
    }

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

    fun logout() {
        scope.launch {
            localRepository.removeUid()
            val result = logoutUseCase.invoke()
            if(result.isSuccess) {
                _state.update {
                    it.copy(isLoading = false, logout = true)
                }
            } else {
                _state.update {
                    it.copy(isLoading = false)
                }
            }
        }
    }
}
