package com.rgr.rutappa.app.di

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rgr.rutappa.data.local.dataStorePreferences
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.dsl.module

val providerModule = module {
    single<DataStore<Preferences>> {
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.Main),
            migrations = emptyList(),
            calculatedPath = ""
        )
    }
}


