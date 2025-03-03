package com.rgr.rutappa.domain.provider

import com.rgr.rutappa.app.state.TapaLocation
import com.rgr.rutappa.domain.model.ResultKMM

interface LocationProvider {
    fun requestPermission()
    suspend fun hasPermission(): Boolean?
    suspend fun getLocation(): ResultKMM<TapaLocation>
    fun isLocationActive(): Boolean
    fun activeLocation()
    fun areCoordinatesWithinDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        maxDistance: Float
    ): Boolean
}