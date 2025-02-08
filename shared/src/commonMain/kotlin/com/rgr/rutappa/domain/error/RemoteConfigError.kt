package com.rgr.rutappa.domain.error

sealed class RemoteConfigError: Error() {
    data object NoError: RemoteConfigError()
    data object ItemNotFound: RemoteConfigError()
    data object NoActivityError: RemoteConfigError()
    data object RemoteConfigTaskFailed: RemoteConfigError()
    data object LogoutFailed: RemoteConfigError()
}