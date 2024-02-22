package com.rgr.rutappa.app.state

import com.rgr.rutappa.app.navigation.Routes

sealed class SplashState {
    data object Init: SplashState()
    data class Finished(val route: Routes): SplashState()
}