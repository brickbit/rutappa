package com.rgr.rutappa.domain.provider

import com.rgr.rutappa.domain.model.ResultKMM

interface LocationProvider {
    fun requestPermission()
    fun hasPermission(): Boolean
    suspend fun getLocation(): ResultKMM<Pair<String, String>>
    fun isLocationActive(): Boolean
    fun activeLocation()
}