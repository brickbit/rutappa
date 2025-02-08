package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.TapaItemBo

data class MainState(
    val isLoading: Boolean = false,
    val tapas: List<TapaItemBo> = emptyList(),
    val filteredTapas: List<TapaItemBo> = emptyList(),
    val logout: Boolean = false,
    val provinces: List<String> = emptyList(),
    val error: RemoteConfigError? = null
)
