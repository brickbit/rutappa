package com.rgr.rutappa.domain.repository;

import androidx.datastore.preferences.core.Preferences

interface LocalRepository {
    suspend fun saveUid(mail: String): Preferences
    suspend fun getUid(): String
    suspend fun removeUid(): Preferences

    suspend fun addTapaVoted(tapa: String): Preferences
    suspend fun getTapaVoted(): Set<String>
    suspend fun removeTapaVoted(): Preferences

}