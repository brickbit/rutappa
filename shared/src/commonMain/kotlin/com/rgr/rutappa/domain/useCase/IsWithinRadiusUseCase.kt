package com.rgr.rutappa.domain.useCase
import com.rgr.rutappa.domain.provider.LocationProvider

class IsWithinRadiusUseCase(
    private val repository: LocationProvider
) {

    operator fun invoke(
        deviceLat: String,
        deviceLon: String,
        localLat: String?,
        localLon: String?
    ): Boolean {
        val lat1 = localLat?.toDouble()
        val lon1 = localLon?.toDouble()
        val lat2 = deviceLat.toDouble()
        val lon2 = deviceLon.toDouble()
        if(lat1 == null || lon1 == null) {
            return false
        }
        return repository.areCoordinatesWithinDistance(lat1 = lat1, lon1 = lon1, lat2 = lat2, lon2 = lon2, maxDistance = 300.0f)
    }
}