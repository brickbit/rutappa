package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.MainState
import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.repository.LocalRepository
import com.rgr.rutappa.domain.useCase.DeleteAccountUseCase
import com.rgr.rutappa.domain.useCase.GetTapaListUseCase
import com.rgr.rutappa.domain.useCase.LogoutUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class MainViewModel(
    private val getTapaUseCase: GetTapaListUseCase,
    private val logoutUseCase: LogoutUseCase,
    private val deleteAccountUseCase: DeleteAccountUseCase,
    private val localRepository: LocalRepository,
): BaseViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState())
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainState()
    ).toCommonStateFlow()

    fun getListTapas(configuration: Int) {
        scope.launch {
            _state.update { it.copy(isLoading = false) }
            when(val result = getTapaUseCase.invoke(configuration)) {
                is ResultKMM.Success -> {
                    val provinces = result.data.map { tapa -> tapa.local.province }.toSet().sorted().toMutableList()
                    provinces.add(0, ALL_TAPAS)
                    _state.update {
                        it.copy(
                            tapas = result.data,
                            filteredTapas = result.data,
                            provinces = provinces,
                            isLoading = false
                        )
                    }
                }
                is ResultKMM.Failure -> {
                    when(result.error) {
                        RemoteConfigError.NoError -> { /*Nothing to do here*/ }
                        RemoteConfigError.ItemNotFound -> {
                            _state.update {
                                it.copy(isLoading = false, error = RemoteConfigError.ItemNotFound)
                            }
                        }
                        RemoteConfigError.NoActivityError -> {
                            _state.update {
                                it.copy(isLoading = false, error = RemoteConfigError.NoActivityError)
                            }
                        }
                        RemoteConfigError.RemoteConfigTaskFailed -> {
                            _state.update {
                                it.copy(isLoading = false, error = RemoteConfigError.RemoteConfigTaskFailed)
                            }
                        }
                    }
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
                    it.copy(isLoading = false, error = RemoteConfigError.LogoutFailed)
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
                    it.copy(isLoading = false, error = RemoteConfigError.LogoutFailed)
                }
            }
        }
    }

    fun filterTapa(filter: String) {
        _state.update {
            it.copy(
                filteredTapas =
                    if (filter == ALL_TAPAS) {
                        state.value.tapas
                    } else {
                        state.value.tapas.filter { tapa -> tapa.local.province == filter }
                    }
            )
        }
    }

    companion object {
        const val ALL_TAPAS = "Todas"
    }
}