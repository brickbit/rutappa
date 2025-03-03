package com.rgr.rutappa.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import com.rgr.rutappa.domain.repository.LocalRepository
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class LocalRepositoryImpl(
    private val dataStore: DataStore<Preferences>
): LocalRepository {
    private companion object {
        private const val PREFS_TAG_KEY = "AppPreferences"
        private const val MAIL = "MAIL"
        private const val TAPA_VOTED = "TAPA_VOTED"
        private const val HAS_PERMISSION = "HAS_PERMISSION"
    }

    private val preferredMail = stringPreferencesKey("$PREFS_TAG_KEY$MAIL")
    private val tapaVoted = stringSetPreferencesKey("$PREFS_TAG_KEY$TAPA_VOTED")
    private val hasPermission = booleanPreferencesKey("$PREFS_TAG_KEY$HAS_PERMISSION")


    override suspend fun saveUid(mail: String): Preferences = dataStore.edit { preferences ->
        preferences[preferredMail] = mail
    }

    override suspend fun getUid(): String = dataStore.data.map { preferences ->
        preferences[preferredMail] ?: ""
    }.first()

    override suspend fun removeUid(): Preferences = dataStore.edit { preferences ->
        preferences[preferredMail] = ""
    }

    override suspend fun addTapaVoted(tapa: String): Preferences = dataStore.edit { preferences ->
        val data = getTapaVoted().toMutableList()
        data.add(tapa)
        val set = data.toSet()
        preferences[tapaVoted] = set
    }

    override suspend fun getTapaVoted(): Set<String> = dataStore.data.map { preferences ->
        preferences[tapaVoted] ?: emptySet()
    }.first()

    override suspend fun removeTapaVoted(): Preferences = dataStore.edit { preferences ->
        preferences[tapaVoted] = emptySet()
    }

    override suspend fun savePermissionStatus(status: Boolean?) {
        if(status != null) {
            dataStore.edit { preferences ->
                preferences[hasPermission] = status
            }
        }
    }

    override suspend fun getPermissionStatus(): Boolean? = dataStore.data.map { preferences ->
        preferences[hasPermission]
    }.first()
}