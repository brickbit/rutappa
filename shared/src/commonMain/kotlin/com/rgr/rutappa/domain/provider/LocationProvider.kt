package com.rgr.rutappa.domain.provider

interface LocationProvider {
    fun requestPermission()
    fun hasPermission(): Boolean
    fun getLocation(callback: (String?, String?) -> Unit)
    fun isLocationActive(): Boolean
    fun activeLocation()
    fun areCoordinatesWithinDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        maxDistance: Float
    ): Boolean
}