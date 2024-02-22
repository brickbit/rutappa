package com.rgr.rutappa.app.state

sealed class LoginState {
    data object NotLogged: LoginState()
    data object Loading: LoginState()
    data class Logged(val mail: String): LoginState()
}