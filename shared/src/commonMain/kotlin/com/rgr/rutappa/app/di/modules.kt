package com.rgr.rutappa.app.di

import com.rgr.rutappa.data.local.LocalRepositoryImpl
import com.rgr.rutappa.data.repository.TapaRepositoryImpl
import com.rgr.rutappa.domain.repository.LocalRepository
import com.rgr.rutappa.domain.repository.TapaRepository
import com.rgr.rutappa.domain.useCase.DeleteAccountUseCase
import com.rgr.rutappa.domain.useCase.GetTapaDetailUseCase
import com.rgr.rutappa.domain.useCase.GetTapaListUseCase
import com.rgr.rutappa.domain.useCase.IsUserLoggedUseCase
import com.rgr.rutappa.domain.useCase.LogoutUseCase
import com.rgr.rutappa.domain.useCase.ResultUseCase
import com.rgr.rutappa.domain.useCase.SignInUseCase
import com.rgr.rutappa.domain.useCase.SignInWithIntentUseCase
import com.rgr.rutappa.domain.useCase.TapaVotedUseCase
import com.rgr.rutappa.domain.useCase.VoteUseCase
import org.koin.dsl.module

val repositoryModule = module {
    factory<TapaRepository> { TapaRepositoryImpl() }
    factory<LocalRepository> { LocalRepositoryImpl(get()) }
}

val useCaseModule = module {
    single { GetTapaListUseCase(get()) }
    single { GetTapaDetailUseCase(get()) }
    single { IsUserLoggedUseCase(get()) }
    single { SignInUseCase(get()) }
    single { SignInWithIntentUseCase(get(), get()) }
    single { VoteUseCase(get(), get()) }
    single { TapaVotedUseCase(get()) }
    single { DeleteAccountUseCase(get(), get(), get()) }
    single { LogoutUseCase(get()) }
    single { ResultUseCase(get()) }
}