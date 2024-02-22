package com.rgr.rutappa.domain.error

sealed class LoginError: Error() {
    data object NoError: LoginError()
    data object UserNotLogged: LoginError()
    data object UnableToSignIn: LoginError()
    data object LoginCancelled: LoginError()
    data object UnableToLogout: LoginError()
    data object UnableToDeleteUser: LoginError()

}