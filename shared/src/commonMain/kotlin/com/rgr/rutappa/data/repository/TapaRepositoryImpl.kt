package com.rgr.rutappa.data.repository

import com.rgr.rutappa.domain.model.LocalItemBo
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.repository.TapaRepository
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class TapaRepositoryImpl: TapaRepository {
    override fun getAllTapas(): Result<List<TapaItemBo>> {
        val a = Json.encodeToString(tapas)
        return Result.success(tapas)
    }

    override fun getTapaById(id: String): Result<TapaItemBo> {
        return Result.success(tapas.first { it.id == id })
    }

    override fun voteTapa(id: String, score: Int) {
        TODO("Not yet implemented")
    }
}

val tapas = listOf(
    TapaItemBo(
        id = "1",
        name = "Nombre de tapa 1",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Lentejas", "Garbanzos"),
        local = LocalItemBo(
            id = "1",
            name = "Taberna los Cazurros",
            province = "León",
            instagram = "",
            facebook = "",
            longitude = "-6.379673",
            latitude = "39.466362"
        )
    ),
    TapaItemBo(
        id = "2",
        name = "Nombre de tapa largo como muestra de texto largo para mock",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Lentejas", "Garbanzos", "alubias"),
        local = LocalItemBo(
            id = "1",
            name = "Restaurante tapería lio Salamanca",
            province = "Salamanca",
            instagram = "",
            facebook = "",
            longitude = "-6.380052",
            latitude = "39.455370"
        )
    ),
    TapaItemBo(
        id = "3",
        name = "Nombre de tapa 3",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Garbanzo"),
        local = LocalItemBo(
            id = "1",
            name = "Tapería de la Vega",
            province = "Salamanca",
            instagram = "",
            facebook = "",
            longitude = "-6.380052",
            latitude = "39.455370"
        )
    ),
    TapaItemBo(
        id = "4",
        name = "Nombre de tapa 4",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Lentejas", "Alubias"),
        local = LocalItemBo(
            id = "1",
            name = "La Tasquita",
            province = "Valladolid",
            instagram = "",
            facebook = "",
            longitude = "-6.380052",
            latitude = "39.455370"
        )
    ),
    TapaItemBo(
        id = "5",
        name = "Nombre de tapa 1",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Lentejas", "Garbanzos"),
        local = LocalItemBo(
            id = "1",
            name = "Salamanca",
            province = "Salamanca",
            instagram = "",
            facebook = "",
            longitude = "-6.380052",
            latitude = "39.455370"
        )
    ),
    TapaItemBo(
        id = "6",
        name = "Nombre de tapa largo como muestra de texto largo para mock",
        photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
        shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
        legumes = listOf("Lentejas", "Garbanzos"),
        local = LocalItemBo(
            id = "1",
            name = "El pez de San Lorenzo",
            province = "Burgos",
            instagram = "",
            facebook = "",
            longitude = "-6.380052",
            latitude = "39.455370"
        )
    ),
)