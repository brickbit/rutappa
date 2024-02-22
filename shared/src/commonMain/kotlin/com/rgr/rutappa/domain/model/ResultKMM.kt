package com.rgr.rutappa.domain.model

sealed class ResultKMM<out T> {
    data class Success<T>(val data: T): ResultKMM<T>()
    data class Failure(val error: Error): ResultKMM<Nothing>()
}