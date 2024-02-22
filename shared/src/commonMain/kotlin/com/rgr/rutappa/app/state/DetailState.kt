package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.TapaItemBo

sealed class DetailState {
    data object Loading: DetailState()
    data class Loaded(val tapa: TapaItemBo, val voted: Boolean): DetailState()
    data class Voted(val tapa: TapaItemBo, val voted: Boolean): DetailState()

}