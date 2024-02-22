package com.rgr.rutappa.app.viewModel

import com.rgr.rutappa.app.flow.toCommonStateFlow
import com.rgr.rutappa.app.navigation.Routes
import com.rgr.rutappa.app.state.SplashState
import com.rgr.rutappa.domain.useCase.IsUserLoggedUseCase
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class SplashViewModel(
    private val isUserLoggedUseCase: IsUserLoggedUseCase
): BaseViewModel() {
    private val _state: MutableStateFlow<SplashState> = MutableStateFlow(SplashState.Init)
    val state = _state.stateIn(
        scope = scope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = SplashState.Init
    ).toCommonStateFlow()

    init {
        initializeSplash()
    }

    private fun initializeSplash() {
        scope.launch {
            delay(3000)
            isUserLoggedUseCase.invoke()
                .onSuccess {
                    _state.update {
                        SplashState.Finished(Routes.Main)
                    }
                }
                .onFailure {
                    _state.update {
                        SplashState.Finished(Routes.Login)
                    }
                }
        }
    }
}