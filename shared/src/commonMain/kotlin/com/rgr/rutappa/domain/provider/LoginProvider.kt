package com.rgr.rutappa.domain.provider

import at.asitplus.KmmResult

interface LoginProvider {
    fun getSignedInUser(): String?
    suspend fun signIn(): KmmResult<Unit>
    suspend fun signInWithIntent(): KmmResult<String>
    suspend fun logout(): KmmResult<Unit>
    suspend fun deleteAccount(): KmmResult<Unit>

}