package com.rgr.rutappa.android.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import com.rgr.rutappa.android.provider.ActivityProvider
import com.rgr.rutappa.android.provider.FirebaseRemoteDataProviderImpl
import com.rgr.rutappa.android.provider.FirestoreProviderImpl
import com.rgr.rutappa.android.provider.IntentProvider
import com.rgr.rutappa.android.provider.IntentSenderProvider
import com.rgr.rutappa.android.provider.LoginProviderImpl
import com.rgr.rutappa.app.viewModel.DetailViewModel
import com.rgr.rutappa.app.viewModel.LoginViewModel
import com.rgr.rutappa.app.viewModel.MainViewModel
import com.rgr.rutappa.app.viewModel.SplashViewModel
import com.rgr.rutappa.data.local.SETTINGS_PREFERENCES
import com.rgr.rutappa.data.local.dataStorePreferences
import com.rgr.rutappa.domain.provider.FirebaseRemoteDataProvider
import com.rgr.rutappa.domain.provider.FirestoreProvider
import com.rgr.rutappa.domain.provider.LoginProvider
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module
import java.io.File

fun providerModules(context: Context) = module {
    single<DataStore<Preferences>> {
        dataStorePreferences(
            corruptionHandler = null,
            coroutineScope = CoroutineScope(SupervisorJob() + Dispatchers.IO),
            migrations = emptyList(),
            calculatedPath = File(context.filesDir, "datastore/$SETTINGS_PREFERENCES").path
        )
    }
    single { ActivityProvider() }
    single { IntentSenderProvider() }
    single { IntentProvider() }
    factory<FirebaseRemoteDataProvider>{ FirebaseRemoteDataProviderImpl(get()) }
    factory<FirestoreProvider>{ FirestoreProviderImpl() }
    factory<LoginProvider> { LoginProviderImpl(context, get(), get()) }
}

val viewModelModules = module {
    viewModel { SplashViewModel(get()) }
    viewModel { LoginViewModel(get(), get()) }
    viewModel { MainViewModel(get(), get(), get()) }
    viewModel { DetailViewModel(get(), get(), get()) }
}