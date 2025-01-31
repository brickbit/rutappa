package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.LocationProvider

class GetLocationUseCase(
    private val repository: LocationProvider
) {
    operator fun invoke(): ResultKMM<Pair<String, String>> {
        if(!repository.hasPermission()) {
            repository.requestPermission()
        }
        return repository.getLocation()
    }
}