package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.TapaItemBo

sealed class MainState {
    data object Loading: MainState()
    data class Loaded(val tapas: List<TapaItemBo>): MainState()
    data object Logout: MainState()
    data object MainStateError: MainState()
}