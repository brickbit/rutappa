package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.provider.LoginProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.IO
import kotlinx.coroutines.withContext
import at.asitplus.KmmResult

class SignInUseCase(
    private val loginProvider: LoginProvider
) {
    suspend operator fun invoke(): KmmResult<Unit> {
        return withContext(Dispatchers.IO) {
            loginProvider.signIn()
        }
    }
}