package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryPartnerBO(
    @SerialName("name")
    val name: String,
    @SerialName("background")
    val background: String,
    @SerialName("partners")
    val partners: List<PartnerBO>
)