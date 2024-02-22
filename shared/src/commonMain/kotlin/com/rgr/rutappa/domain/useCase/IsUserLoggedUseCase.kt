package com.rgr.rutappa.domain.useCase

import com.rgr.rutappa.domain.error.LoginError
import com.rgr.rutappa.domain.provider.LoginProvider

class IsUserLoggedUseCase(
    private val loginProvider: LoginProvider
) {
    operator fun invoke(): Result<String> {
        if(loginProvider.getSignedInUser() != null) {
            return Result.success(loginProvider.getSignedInUser()!!)
        }
        return Result.failure(LoginError.UserNotLogged)
    }
}