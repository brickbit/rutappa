package com.rgr.rutappa.domain.useCase

import at.asitplus.KmmResult
import com.rgr.rutappa.domain.provider.FirestoreProvider
import com.rgr.rutappa.domain.provider.LoginProvider
import com.rgr.rutappa.domain.repository.LocalRepository

class DeleteAccountUseCase(
    private val loginProvider: LoginProvider,
    private val firestoreProvider: FirestoreProvider,
    private val localRepository: LocalRepository,
) {

    suspend operator fun invoke(): KmmResult<Unit> {
        val user = localRepository.getUid()
        val result = loginProvider.deleteAccount()
        if(result.isSuccess) {
            localRepository.removeUid()
            localRepository.removeTapaVoted()
            firestoreProvider.removeVote(user)
        }
        return result
    }
}
