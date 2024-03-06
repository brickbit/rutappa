package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TapaResult(
    @SerialName("tapa")
    val tapa: String = "",
    @SerialName("user")
    val user: String = "",
    @SerialName("vote")
    val vote: Int = 0
)
