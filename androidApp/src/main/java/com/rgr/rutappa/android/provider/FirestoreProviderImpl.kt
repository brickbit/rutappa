package com.rgr.rutappa.android.provider

import at.asitplus.KmmResult
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.provider.FirestoreProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlin.coroutines.resume

class FirestoreProviderImpl: FirestoreProvider {
    private val db = Firebase.firestore
    override suspend fun vote(user: String, vote: Int, tapa: String): KmmResult<Unit> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                val userDb = hashMapOf(
                    "user" to user,
                    "vote" to vote,
                    "tapa" to tapa,
                )
                var votedYet = false
                db.collection("tapa_contest")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val dbUser = document.data["user"]
                            val dbTapa = document.data["tapa"]
                            if (dbUser == user && dbTapa == tapa) {
                                votedYet = true
                                cont.resume(KmmResult.failure(FirestoreError.TapaVotedYet))
                            }
                        }
                        if (!votedYet) {
                            db.collection("tapa_contest")
                                .add(userDb)
                                .addOnSuccessListener { _ ->
                                    cont.resume(KmmResult.success(Unit))
                                }
                                .addOnFailureListener { _ ->
                                    cont.resume(KmmResult.failure(FirestoreError.WritingError))
                                }
                        }
                    }
                    .addOnFailureListener { _ ->
                        cont.resume(KmmResult.failure(FirestoreError.ReadingError))
                    }
            }
        }
    }

    override suspend fun removeVote(user: String): KmmResult<Unit> {
        return withContext(Dispatchers.IO) {
            suspendCancellableCoroutine { cont ->
                db.collection("tapa_contest")
                    .get()
                    .addOnSuccessListener { result ->
                        for (document in result) {
                            val dbUser = document.data["user"]
                            if (dbUser == user) {
                                db.collection("tapa_contest").document(document.id).delete()
                            }
                        }
                        cont.resume(KmmResult.failure(FirestoreError.TapaVotedYet))
                    }
                    .addOnFailureListener { _ ->
                        cont.resume(KmmResult.failure(FirestoreError.ReadingError))
                    }
            }
        }
    }


}