package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo
import com.rgr.rutappa.domain.provider.FirebaseRemoteDataProvider


class GetTapaListUseCase(
    private val remoteConfig: FirebaseRemoteDataProvider
) {
    suspend operator fun invoke(configuration: Int): ResultKMM<List<TapaItemBo>> {
        return remoteConfig.getListTapas(configuration)
    }
}