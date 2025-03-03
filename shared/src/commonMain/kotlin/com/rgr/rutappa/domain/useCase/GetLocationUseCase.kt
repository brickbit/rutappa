package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.app.state.TapaLocation
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.LocationProvider

class GetLocationUseCase(
    private val repository: LocationProvider
) {
    suspend operator fun invoke(): ResultKMM<TapaLocation> {
        return repository.getLocation()
    }
}