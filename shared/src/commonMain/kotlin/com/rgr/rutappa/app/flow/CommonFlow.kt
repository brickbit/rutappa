package com.rgr.rutappa.app.flow

import kotlinx.coroutines.flow.Flow

expect class CommonFlow<T>(flow: Flow<T>): Flow<T>

fun <T> Flow<T>.toCommonFlow() = CommonFlow(this)