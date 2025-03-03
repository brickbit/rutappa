package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.TapaItemBo

data class DetailState (
    val isLoading: Boolean = true,
    val logout: Boolean = false,
    val error: Boolean = false,
    val tapa: TapaItemBo? = null,
    val location: TapaLocation? = null,
    val hasLocationPermission: Boolean? = null,
    val isGPSActive: Boolean = false,
    val isInRadius: Boolean? = null,
    val voteStatus: VoteStatus = VoteStatus.UNKNOWN
)

enum class VoteStatus {
    UNKNOWN,
    CAN_VOTE,
    VOTED
}

data class TapaLocation(
    val latitude: String,
    val longitude: String
)