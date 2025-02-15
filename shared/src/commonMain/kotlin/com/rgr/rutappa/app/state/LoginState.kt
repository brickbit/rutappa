package com.rgr.rutappa.app.state

data class LoginState (
    val isLoading: Boolean = false,
    val logged: Boolean = false,
    val mail: String = ""
)
