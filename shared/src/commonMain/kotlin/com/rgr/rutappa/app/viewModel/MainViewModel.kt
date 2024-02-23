package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.MainState
import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.ResultKMM
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
    private val deleteAccountUseCase: DeleteAccountUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<MainState> = MutableStateFlow(MainState.Loading)
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = MainState.Loading
    ).toCommonStateFlow()

    private val _errorState: MutableStateFlow<RemoteConfigError> = MutableStateFlow(RemoteConfigError.NoError)
    val errorState = _errorState.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = RemoteConfigError.NoError
    ).toCommonStateFlow()

    fun getListTapas(configuration: Int) {
        scope.launch {
            val result = getTapaUseCase.invoke(configuration)
            when(result) {
                is ResultKMM.Success -> {
                    _state.update {
                        MainState.Loaded(result.data)
                    }
                }
                is ResultKMM.Failure -> {
                    when(result.error) {
                        RemoteConfigError.NoError -> { /*Nothing to do here*/ }
                        RemoteConfigError.ItemNotFound -> {
                            _errorState.update {
                                RemoteConfigError.ItemNotFound
                            }
                        }
                        RemoteConfigError.NoActivityError -> {
                            _errorState.update {
                                RemoteConfigError.NoActivityError
                            }
                        }
                        RemoteConfigError.RemoteConfigTaskFailed -> {
                            _errorState.update {
                                RemoteConfigError.RemoteConfigTaskFailed
                            }
                        }
                    }
                }
            }
        }
    }

    fun logout() {
        scope.launch {
            val result = logoutUseCase.invoke()
            if(result.isSuccess) {
                _state.update {
                    MainState.Logout
                }
            } else {
                _state.update {
                    MainState.MainStateError
                }
            }
        }
    }

    fun deleteAccount() {
        scope.launch {
            _state.update {
                MainState.Loading
            }
            val result = deleteAccountUseCase.invoke()
            if(result.isSuccess) {
                _state.update {
                    MainState.Logout
                }
            } else {
                _state.update {
                    MainState.MainStateError
                }
            }
        }
    }
}