package com.rgr.rutappa.android.app

import android.app.Application
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.disk.DiskCache
import coil.memory.MemoryCache
import coil.request.CachePolicy
import coil.util.DebugLogger
import com.rgr.rutappa.android.di.providerModules
import com.rgr.rutappa.android.di.viewModelModules
import com.rgr.rutappa.app.di.repositoryModule
import com.rgr.rutappa.app.di.useCaseModule
import org.koin.core.context.startKoin

class Rutappa: Application() {
    override fun onCreate() {
        super.onCreate()
        startKoin {
            modules(
                providerModules(applicationContext),
                repositoryModule,
                useCaseModule,
                viewModelModules
            )
        }
    }
}