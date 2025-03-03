package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LocationProvider

class RequestLocationPermissionUseCase(
    private val repository: LocationProvider
)  {
    operator fun invoke(onFinish: () -> Unit) {
        repository.requestPermission()
        onFinish()
    }
}