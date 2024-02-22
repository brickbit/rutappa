package com.rgr.rutappa.domain.provider

import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.model.TapaItemBo

interface FirebaseRemoteDataProvider {
    suspend fun getListTapas(configuration: Int): ResultKMM<List<TapaItemBo>>

    suspend fun getTapaDetail(configuration: Int, id: String): ResultKMM<TapaItemBo>
}