package com.rgr.rutappa.domain.provider

import com.rgr.rutappa.domain.model.TapaResult

interface DocumentProvider {
    fun writeJson(result: List<TapaResult>)
}