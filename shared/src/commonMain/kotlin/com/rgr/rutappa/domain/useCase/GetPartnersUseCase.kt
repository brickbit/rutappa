package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.PartnersListBO
import com.rgr.rutappa.domain.provider.FirebaseRemoteDataProvider

class GetPartnersUseCase(
    private val remoteConfig: FirebaseRemoteDataProvider
) {
    suspend operator fun invoke(configuration: Int): ResultKMM<PartnersListBO> {
        return remoteConfig.getPartners(configuration)
    }
}