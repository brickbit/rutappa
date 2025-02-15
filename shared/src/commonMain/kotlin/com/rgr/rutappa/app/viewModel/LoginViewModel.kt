package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.state.LoginState
import com.rgr.rutappa.domain.error.LoginError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.repository.LocalRepository
import com.rgr.rutappa.domain.useCase.SignInUseCase
import com.rgr.rutappa.domain.useCase.SignInWithIntentUseCase
import io.ktor.util.PlatformUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class LoginViewModel(
    private val signInUseCase: SignInUseCase,
    private val signInWithIntentUseCase: SignInWithIntentUseCase,
    private val localRepository: LocalRepository,

): BaseViewModel() {
    private val _state: MutableStateFlow<LoginState> = MutableStateFlow(LoginState())
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginState()
    ).toCommonStateFlow()

    private val _errorState: MutableStateFlow<LoginError> = MutableStateFlow(LoginError.NoError)
    val errorState = _errorState.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = LoginError.NoError
    ).toCommonStateFlow()

    fun signIn() {
        scope.launch {
            _state.update { it.copy(isLoading = true) }
            val result = signInUseCase.invoke()
            if (result.isSuccess) {
                _state.update { it.copy(isLoading = false, logged = PlatformUtils.IS_JVM) }
            } else {
                _state.update { it.copy(isLoading = false, logged = false) }
                result.exceptionOrNull()?.let {
                    processError(it)
                }
            }
        }
    }

    fun signInWithDevice(uuid: String) {
        scope.launch {
            localRepository.saveUid(uuid)
            _state.update { it.copy(isLoading = false, logged = true, mail = uuid) }
        }
    }

    fun signInWithIntent() {
        scope.launch {
            val result = signInWithIntentUseCase.invoke()
            if (result.isSuccess) {
                result.getOrNull()?.let { signData ->
                    _state.update { it.copy(isLoading = false, logged = true, mail = signData) }
                }
            } else {
                _state.update { it.copy(isLoading = false, logged = false) }
                result.exceptionOrNull()?.let {
                    processError(it)
                }
            }
        }
    }

    private fun processError(throwable: Throwable) {
        when (throwable as LoginError) {
            LoginError.LoginCancelled -> {
                _errorState.update {
                    LoginError.LoginCancelled
                }
            }
            LoginError.NoError -> { /*Nothing to do here*/ }
            LoginError.UnableToSignIn -> {
                _errorState.update {
                    LoginError.UnableToSignIn
                }
            }
            LoginError.UserNotLogged -> {
                _errorState.update {
                    LoginError.UserNotLogged
                }
            }
            LoginError.UnableToDeleteUser -> {
                _errorState.update {
                    LoginError.UnableToDeleteUser
                }
            }
            LoginError.UnableToLogout -> {
                _errorState.update {
                    LoginError.UnableToLogout
                }
            }
        }
    }

    fun clearError() {
        _errorState.update {
            LoginError.NoError
        }
    }
}