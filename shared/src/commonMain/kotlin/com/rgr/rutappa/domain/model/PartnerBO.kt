package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnerBO(
    @SerialName("image")
    val image: String,
    @SerialName("link")
    val link: String
)