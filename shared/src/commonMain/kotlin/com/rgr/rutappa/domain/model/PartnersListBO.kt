package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class PartnersListBO(
    @SerialName("categories")
    val categories: List<CategoryPartnerBO>
)