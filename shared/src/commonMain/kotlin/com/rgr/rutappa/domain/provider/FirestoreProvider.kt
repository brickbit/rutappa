package com.rgr.rutappa.domain.provider

import at.asitplus.KmmResult
import com.rgr.rutappa.domain.model.TapaResult

interface FirestoreProvider {
    suspend fun vote(user: String, vote: Int, tapa: String): KmmResult<Unit>

    suspend fun removeVote(user: String): KmmResult<Unit>

    suspend fun getResult(): KmmResult<List<TapaResult?>>
}