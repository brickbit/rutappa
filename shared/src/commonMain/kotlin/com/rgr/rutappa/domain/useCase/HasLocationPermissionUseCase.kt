package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LocationProvider

class HasLocationPermissionUseCase(
    private val repository: LocationProvider
) {
    operator fun invoke(): Boolean {
        return repository.hasPermission()
    }
}