package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.PartnersListBO

data class PartnerState (
    val isLoading: Boolean = true,
    val error: Boolean = false,
    val logout: Boolean = false,
    val partners: PartnersListBO? = null
)