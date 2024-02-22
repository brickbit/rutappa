package com.rgr.rutappa.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
@Serializable
data class TapaItemBo(
    @SerialName("id")
    val id: String,
    @SerialName("name")
    val name: String,
    @SerialName("photo")
    val photo: String,
    @SerialName("shortDescription")
    val shortDescription: String,
    @SerialName("legumes")
    val legumes: List<String>,
    @SerialName("local")
    val local: LocalItemBo,
)
