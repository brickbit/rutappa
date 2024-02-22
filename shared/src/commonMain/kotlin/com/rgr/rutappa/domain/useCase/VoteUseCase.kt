package com.rgr.rutappa.domain.useCase

import at.asitplus.KmmResult
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.FirestoreProvider
import com.rgr.rutappa.domain.repository.LocalRepository

class VoteUseCase(
    private val localRepository: LocalRepository,
    private val firestoreProvider: FirestoreProvider
) {
    suspend operator fun invoke(
        vote: Int,
        tapa: String
    ): KmmResult<Unit> {
        val user = localRepository.getUid()
        return if(user.isNotEmpty()) {
            val result = firestoreProvider.vote(user, vote, tapa)
            if(result.isSuccess) {
                localRepository.addTapaVoted(tapa)
                return result
            }
            return result
        } else {
            KmmResult.failure(FirestoreError.NoUserError)
        }
    }
}