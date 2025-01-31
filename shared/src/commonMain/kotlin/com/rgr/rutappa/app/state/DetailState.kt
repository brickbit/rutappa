package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.TapaItemBo

data class DetailState (
    val isLoading: Boolean = true,
    val tapa: TapaItemBo? = null,
    val voted: Boolean = false,
    val location: Pair<String,String>? = null,
    val canVote: Boolean = false,
    val isWithinRadius: Boolean = false
)