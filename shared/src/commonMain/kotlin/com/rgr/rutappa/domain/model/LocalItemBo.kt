package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class LocalItemBo(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("province")
    val province: String,
    @SerialName("instagram")
    val instagram: String,
    @SerialName("facebook")
    val facebook: String,
    @SerialName("longitude")
    val longitude: String = "",
    @SerialName("latitude")
    val latitude: String = "",
)