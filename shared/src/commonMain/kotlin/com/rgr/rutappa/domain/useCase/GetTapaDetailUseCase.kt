package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.provider.FirebaseRemoteDataProvider

class GetTapaDetailUseCase(
    private val remoteConfig: FirebaseRemoteDataProvider
) {

    suspend operator fun invoke(configuration: Int, id: String): ResultKMM<TapaItemBo> {
        return remoteConfig.getTapaDetail(configuration, id)
    }
}