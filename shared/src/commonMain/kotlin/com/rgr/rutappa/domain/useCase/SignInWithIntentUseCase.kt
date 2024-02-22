package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LoginProvider
import com.rgr.rutappa.domain.repository.LocalRepository
import at.asitplus.KmmResult

class SignInWithIntentUseCase(
    private val loginProvider: LoginProvider,
    private val localRepository: LocalRepository
) {
    suspend operator fun invoke(): KmmResult<String> {
        val result = loginProvider.signInWithIntent()
        if(result.isSuccess) {
            result.getOrNull()?.let {
                localRepository.saveUid(it)
            }
        }
        return result
    }
}