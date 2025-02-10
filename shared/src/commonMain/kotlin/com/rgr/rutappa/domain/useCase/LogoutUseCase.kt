package com.rgr.rutappa.domain.useCase

import at.asitplus.KmmResult
import com.rgr.rutappa.domain.provider.LoginProvider

class LogoutUseCase(
    private val loginProvider: LoginProvider
) {
    suspend operator fun invoke(): KmmResult<Unit> {
        return loginProvider.logout()
    }
}