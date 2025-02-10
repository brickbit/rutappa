package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LocationProvider

class GetLocationUseCase(
    private val repository: LocationProvider
) {
    operator fun invoke(callback: (String?, String?) -> Unit) {
        repository.getLocation(callback)
    }
}