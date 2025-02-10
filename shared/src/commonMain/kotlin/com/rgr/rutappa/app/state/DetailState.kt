package com.rgr.rutappa.app.state

import com.rgr.rutappa.domain.model.TapaItemBo

data class DetailState (
    val isLoading: Boolean = true,
    val tapa: TapaItemBo? = null,
    val location: Pair<String,String>? = null,
    val voteStatus: VoteStatus = VoteStatus.UNKNOWN
)

enum class VoteStatus {
    UNKNOWN,
    LOCATION_INACTIVE,
    LOCATION_NOT_ALLOW,
    UNABLE_OBTAIN_LOCATION,
    OUT_OF_RANGE,
    CAN_VOTE,
    VOTED
}