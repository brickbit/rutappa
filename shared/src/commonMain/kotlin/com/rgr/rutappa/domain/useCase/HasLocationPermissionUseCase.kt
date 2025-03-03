package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LocationProvider

class HasLocationPermissionUseCase(
    private val repository: LocationProvider
) {
    suspend operator fun invoke(): Boolean? {
        return repository.hasPermission()
    }
}