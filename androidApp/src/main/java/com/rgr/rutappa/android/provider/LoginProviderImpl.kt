package com.rgr.rutappa.android.provider

import android.content.Context
import at.asitplus.KmmResult
import com.google.android.gms.auth.api.identity.BeginSignInRequest
import com.google.android.gms.auth.api.identity.BeginSignInRequest.GoogleIdTokenRequestOptions
import com.google.android.gms.auth.api.identity.Identity
import com.google.android.gms.auth.api.identity.SignInClient
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.firebase.auth.GoogleAuthCredential
import com.google.firebase.auth.GoogleAuthProvider
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.rgr.rutappa.android.BuildConfig
import com.rgr.rutappa.domain.error.LoginError
import com.rgr.rutappa.domain.provider.LoginProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.util.concurrent.CancellationException
import kotlin.coroutines.resume


class LoginProviderImpl(
    private val context: Context,
    private val intentSenderProvider: IntentSenderProvider,
    private val intentProvider: IntentProvider
): LoginProvider {
    private val oneTapClient: SignInClient = Identity.getSignInClient(context)
    private val auth = Firebase.auth

    override suspend fun signIn(): KmmResult<Unit> {
        return withContext(Dispatchers.IO) {
            return@withContext try {
                val operation = oneTapClient.beginSignIn(
                    buildSignInRequest()
                )
                operation.addOnCanceledListener {
                    intentSenderProvider.setIntentSender(null)
                }
                val result = operation.await()
                intentSenderProvider.setIntentSender(result?.pendingIntent?.intentSender)
                KmmResult.success(Unit)
            } catch (e: Exception) {
                e.printStackTrace()
                oneTapClient.signOut()
                intentSenderProvider.setIntentSender(null)
                KmmResult.failure(LoginError.UnableToSignIn)
            }
        }
    }

    override suspend fun signInWithIntent(): KmmResult<String> {
        val intent = intentProvider.getIntent()
        val credential = oneTapClient.getSignInCredentialFromIntent(intent)
        val googleIdToken = credential.googleIdToken
        val googleCredential = GoogleAuthProvider.getCredential(googleIdToken, null)
        return try {
            val user = auth.signInWithCredential(googleCredential).await().user
            return  KmmResult.success(user?.uid ?: "")
        } catch(e: Exception) {
            e.printStackTrace()
            if(e is CancellationException) throw e
            KmmResult.failure(LoginError.LoginCancelled)
        }
    }

    override suspend fun logout(): KmmResult<Unit> {
        return try {
            oneTapClient.signOut().await()
            auth.signOut()
            KmmResult.success(Unit)
        } catch (e: Exception) {
            e.printStackTrace()
            KmmResult.failure(LoginError.UnableToLogout)
        }
    }

    override suspend fun deleteAccount(): KmmResult<Unit>  = suspendCancellableCoroutine { cont ->
        try {
            val user = auth.currentUser

            user?.let {
                user.delete()
                    .addOnSuccessListener {
                        cont.resume(KmmResult.success(Unit))
                    }
                    .addOnFailureListener {
                        cont.resume(KmmResult.failure(LoginError.UnableToDeleteUser))
                    }
            } ?: cont.resume(KmmResult.failure(LoginError.UnableToDeleteUser))
        } catch (e: Exception) {
            e.printStackTrace()
            cont.resume(KmmResult.failure(LoginError.UnableToDeleteUser))
        }
    }

    override fun getSignedInUser(): String? = auth.currentUser?.uid

    private fun buildSignInRequest(): BeginSignInRequest {
        return  BeginSignInRequest.Builder()
            .setGoogleIdTokenRequestOptions(
                GoogleIdTokenRequestOptions.Builder()
                    .setSupported(true)
                    .setServerClientId("747962270451-ksshp66gkvcrrpa8b8kivjch4nmcnoh0.apps.googleusercontent.com")
                    .setFilterByAuthorizedAccounts(false)
                    .build()
            )
            .setAutoSelectEnabled(true)
            .build()
    }

}