package com.rgr.rutappa.app.di

import org.koin.core.context.startKoin

fun initKoin() {
    startKoin {
        modules(providerModule)
    }
}
