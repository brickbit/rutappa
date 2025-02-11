package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.LocationProvider

class GetLocationUseCase(
    private val repository: LocationProvider
) {
    suspend operator fun invoke(): ResultKMM<Pair<String, String>> {
        return repository.getLocation()
    }
}