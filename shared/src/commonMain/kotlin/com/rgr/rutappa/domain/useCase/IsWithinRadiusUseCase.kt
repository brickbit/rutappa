package com.rgr.rutappa.domain.useCase
import kotlin.math.*

class IsWithinRadiusUseCase {

    operator fun invoke(deviceCoordinates: Pair<String,String>, localCoordinates: Pair<String?,String?>): Boolean {
        val lat1 = localCoordinates.first?.toDouble()
        val lon1 = localCoordinates.second?.toDouble()
        val lat2 = deviceCoordinates.first.toDouble()
        val lon2 = deviceCoordinates.second.toDouble()
        if(lat1 == null || lon1 == null) {
            return false
        }
        return isWithinRadius(lat1 = lat1, lat2 = lat2, lon1 = lon1, lon2 = lon2, radiusInMeters = 300.0)
    }

    private fun isWithinRadius(
        lat1: Double,
        lon1: Double,
        lat2: Double,
        lon2: Double,
        radiusInMeters: Double
    ): Boolean {
        val R = 6371000.0 // Earth radius in meters
        val dLat = toRadians(lat2 - lat1)
        val dLon = toRadians(lon2 - lon1)
        val a = sin(dLat / 2) * sin(dLat / 2) +
                cos(toRadians(lat1)) * cos(toRadians(lat2)) *
                sin(dLon / 2) * sin(dLon / 2)
        val c = 2 * atan2(sqrt(a), sqrt(1 - a))
        val distance = R * c // Distance in meters

        return distance <= radiusInMeters
    }

    fun toRadians(deg: Double): Double = deg / 180.0 * PI
}