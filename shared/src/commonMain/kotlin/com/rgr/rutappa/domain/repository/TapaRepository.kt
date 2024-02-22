package com.rgr.rutappa.domain.repository

import com.rgr.rutappa.domain.model.TapaItemBo

interface TapaRepository {
    fun getAllTapas(): Result<List<TapaItemBo>>

    fun getTapaById(id: String): Result<TapaItemBo>
    fun voteTapa(id: String, score: Int)
}