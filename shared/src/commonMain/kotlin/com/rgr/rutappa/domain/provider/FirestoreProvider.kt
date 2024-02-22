package com.rgr.rutappa.domain.provider

import at.asitplus.KmmResult

interface FirestoreProvider {
    suspend fun vote(user: String, vote: Int, tapa: String): KmmResult<Unit>

    suspend fun removeVote(user: String): KmmResult<Unit>
}