package com.rgr.rutappa.app.state

data class PartnerState (
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val logout: Boolean = false,
)