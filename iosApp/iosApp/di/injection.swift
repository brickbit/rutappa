//
//  injection.swift
//  iosApp
//
//  Created by Roberto García Romero on 11/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared


//SPLASH
extension IsUserLoggedUseCase {
    static let shared = IsUserLoggedUseCase(loginProvider: LoginProvideImpl.shared)
}

extension SplashViewModel {
    static let shared = SplashViewModel(isUserLoggedUseCase: IsUserLoggedUseCase.shared, localRepository: LocalRepositoryImpl.shared)
}

//LOGIN
extension SignInUseCase {
    static let shared = SignInUseCase(loginProvider: LoginProvideImpl.shared)
}

extension LocalRepositoryImpl {
    static let shared = LocalRepositoryImpl(dataStore: DataStorePreference_nativeKt.dataStorePreferences(corruptionHandler: nil, coroutineScope: nil, migrations: [], calculatedPath: ""))
}

extension SignInWithIntentUseCase {
    static let shared = SignInWithIntentUseCase(loginProvider: LoginProvideImpl.shared, localRepository: LocalRepositoryImpl.shared)
}

extension LoginViewModel {
    static let shared = LoginViewModel(signInUseCase: SignInUseCase.shared, signInWithIntentUseCase: SignInWithIntentUseCase.shared, localRepository: LocalRepositoryImpl.shared)
}

//MAIN
extension LogoutUseCase {
    static let shared = LogoutUseCase(loginProvider: LoginProvideImpl.shared)
}

extension DeleteAccountUseCase {
    static let shared = DeleteAccountUseCase(loginProvider: LoginProvideImpl.shared, firestoreProvider: FirestoreProviderImpl.shared, localRepository: LocalRepositoryImpl.shared)
}

extension GetTapaListUseCase {
    static let shared = GetTapaListUseCase(remoteConfig: FirebaseRemoteDataProviderImpl.shared)
}

extension MainViewModel {
    static let shared = MainViewModel(getTapaUseCase: GetTapaListUseCase.shared, logoutUseCase: LogoutUseCase.shared, deleteAccountUseCase: DeleteAccountUseCase.shared, localRepository: LocalRepositoryImpl.shared)
}

//DETAIL

extension LocationProviderImpl {
    static let shared = LocationProviderImpl()
}

extension GetTapaDetailUseCase {
    static let shared = GetTapaDetailUseCase(remoteConfig: FirebaseRemoteDataProviderImpl.shared)
}

extension VoteUseCase {
    static let shared = VoteUseCase(
        localRepository: LocalRepositoryImpl.shared,
        firestoreProvider: FirestoreProviderImpl.shared
    )
}

extension TapaVotedUseCase {
    static let shared = TapaVotedUseCase(localRepository: LocalRepositoryImpl.shared)
}

extension GetLocationUseCase {
    static let shared = GetLocationUseCase(repository: LocationProviderImpl.shared)
}

extension IsWithinRadiusUseCase {
    static let shared = IsWithinRadiusUseCase()
}
extension DetailViewModel {
    static let shared = DetailViewModel(
        detailUseCase: GetTapaDetailUseCase.shared,
        voteUseCase: VoteUseCase.shared,
        tapaVotedUseCase: TapaVotedUseCase.shared,
        locationUseCase: GetLocationUseCase.shared,
        isWithinRadiusUseCase: IsWithinRadiusUseCase.shared
    )
}
