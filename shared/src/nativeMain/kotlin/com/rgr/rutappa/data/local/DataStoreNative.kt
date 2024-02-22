package com.rgr.rutappa.data.local

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

fun dataStoreNative(): DataStore<Preferences> {
    return dataStorePreferences(
        corruptionHandler = null,
        coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
        migrations = emptyList(),
        calculatedPath = ""
    )
}