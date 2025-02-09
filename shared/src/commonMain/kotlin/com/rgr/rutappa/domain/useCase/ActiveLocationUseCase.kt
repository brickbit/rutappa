package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LocationProvider

class ActiveLocationUseCase(
    private val repository: LocationProvider
) {
    operator fun invoke() {
        repository.activeLocation()
    }
}