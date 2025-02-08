//
//  MainView.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct MainView: View {
    @ObservedObject var viewModel: IOSMainViewModel
    @EnvironmentObject var navigator: Navigator
    @State var isLogout = false
    
    init() {
        self.viewModel = IOSMainViewModel()
    }
    
    var body: some View {
        let _ = Self._printChanges()
    
        VStack(alignment: .leading) {
            mainContent()
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func mainContent() -> AnyView {
        var navigated = false
        if(viewModel.state.isLoading) {
            return AnyView(LoadingView())
        } else {
            if(viewModel.state.logout) {
                return AnyView(LoadingView().task {
                    navigator.navigate(to: .login)
                })
            }
            if(viewModel.state.error == "RemoteConfigError.LogoutFailed") {
                return AnyView(LoadingView().task {
                    navigator.navigate(to: .login)
                })
            }
            return AnyView(
                MainScreen(
                    tapas: viewModel.state.filteredTapas,
                    action: { tapa in
                        Task {
                            navigator.navigate(to: .detail(tapaId: tapa))
                        }
                    },
                    logoutAction:  {
                        viewModel.logout()
                    },
                    deleteAccountAction:  {
                        Task {
                            viewModel.deleteAccount()
                        }
                    },
                    navigateToPartnersAction: {
                        Task {
                            navigator.navigate(to: .partners)
                        }
                    }
                ).overlay(alignment: .top) {
                    Color("secondaryColor")
                        .background(Color("secondaryColor"))
                        .ignoresSafeArea(edges: .top)
                        .frame(height: 0)
                }
            )
        }
    }
}


struct MainScreen: View {
    let tapas: [TapaItemBo]
    let action: (String) -> ()
    let logoutAction: () -> ()
    let deleteAccountAction: () -> ()
    let navigateToPartnersAction: () -> ()
    var logout = UIImage(named: "logout")
    @State private var showingLogout = false
    @State private var showingMenu = false

    var body: some View {
        ZStack {
            ZStack {
                ZStack {
                    Rectangle().fill(
                        LinearGradient(
                            gradient: Gradient(
                                colors: [
                                    Color.white,
                                    Color("primaryColor").opacity(0.6),
                                    Color("primaryColor")
                                ]
                            ),
                            startPoint: .top,
                            endPoint: .bottom
                        )
                    ).frame(maxWidth: .infinity, maxHeight: .infinity)
                        .edgesIgnoringSafeArea(.all)
                    
                }
                ZStack {
                    GeometryReader { geometry in
                        ScrollView {}
                            .safeAreaInset(edge: .top) {
                                VStack{
                                    HeaderView(
                                        hasMenu: true,
                                        onItemClicked: {
                                            showingMenu.toggle()
                                        }
                                    ).padding(.top,1)
                                        .confirmationDialog("Change background", isPresented: $showingLogout) {
                                            Button("Eliminar cuenta") { deleteAccountAction() }
                                            Button("Cerrar sesión") { logoutAction() }
                                            Button("Continuar logado", role: .cancel) { }
                                        } message: {
                                            Text("¿Desea cerrar la sesión o eliminar su cuenta?")
                                        }
                                }
                            }
                            .safeAreaInset(edge: .bottom) {
                                VStack{
                                    SocialWallView()
                                }
                            }
                            .frame(maxWidth: .infinity, alignment: .bottom)
                    }
                    ScrollView {
                        VStack(alignment: .leading) {
                            Text("Las tapas del concurso")
                                .foregroundStyle(Color("secondaryColor"))
                                .font(Font.custom("Berlin Sans FB Demi", size: 18))
                                .padding(.horizontal,24)
                                .padding(.top, 16)
                                .padding(.bottom,16)
                            VStack(alignment: .leading, spacing: 8) {
                                ForEach(tapas, id: \.self) { tapa in
                                    TapaItemList(
                                        tapa: tapa
                                    ).onTapGesture {
                                        action(tapa.id)
                                    }
                                }
                            }
                            .padding(.horizontal, 24)
                        }.ignoresSafeArea()
                    }
                    .padding(.top, 120)
                    .padding(.bottom,100)
                }
            }
            if(showingMenu) {
                Menu(
                    onCloseClicked: {
                        showingMenu.toggle()
                    },
                    onTapasClicked: {
                        showingMenu.toggle()
                    },
                    onPartnersClicked: {
                        showingMenu.toggle()
                        navigateToPartnersAction()
                    },
                    onLogoutClicked: {
                        showingMenu.toggle()
                        showingLogout.toggle()
                    }
                )
            }
        }
    }
}

struct TapaItemList: View {
    let tapa: TapaItemBo
    var body: some View {
        VStack(alignment: .leading) {
            HStack(alignment: .center, spacing: 16) {
                AsyncImage(
                    url: URL(string: tapa.photo),
                    content: { image in
                        image.resizable()
                            .aspectRatio(contentMode: .fill)
                             .frame(maxWidth: 120, maxHeight: 120)
                    },
                    placeholder: {
                        ProgressView()
                            .frame(maxWidth: 120, maxHeight: 120)
                    }
                )
                .clipShape(RoundedRectangle(cornerRadius: 16))
                VStack(alignment: .leading) {
                    Text(tapa.name)
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Montserrat", size: 16))
                    Text("\(tapa.local.name) (\(tapa.local.province))")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Montserrat", size: 12))
                    Text(tapa.shortDescription)
                        .lineLimit(3)
                        .foregroundStyle(Color("secondaryColor").opacity(0.6))
                        .font(Font.custom("Montserrat", size: 12))

                }.frame(maxWidth: 340)
            }
            GeometryReader { metrics in
                Divider()
                    .foregroundStyle(Color("secondaryColor").opacity(0.3))
                    .frame(maxWidth: metrics.size.width * 0.9)
                    .padding(.vertical, 8)
            }
        }
    }
}

struct MainView_Previews: PreviewProvider {
    static var previews: some View {
        MainView()
    }
}

extension MainView {
    @MainActor class IOSMainViewModel: ObservableObject {
        private let viewModel: MainViewModel
        private let localRepository: LocalRepository = LocalRepositoryImpl.shared
        private let loginProvider: LoginProvider = LoginProvideImpl.shared
        private let firestoreProvider: FirestoreProvider = FirestoreProviderImpl.shared

        @Published var state: MainStateSwift = MainStateSwift()
        
        private var handle: DisposableHandle?

        init() {
            self.viewModel = MainViewModel.shared
            self.viewModel.getListTapas(configuration: 0)
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = MainStateSwift(
                        isLoading: state.isLoading,
                        tapas: state.tapas,
                        filteredTapas: state.filteredTapas,
                        logout: state.logout,
                        provinces: state.provinces,
                        error: state.error?.message
                    )
                }
            })
        }
        
        func logout() {
            print("########## logout")
            viewModel.logout()
        }
        
        func deleteAccount() {
            //viewModel.deleteAccount() FAILS COROUTINE
            print("########## deleteAccount")
            Task {
                do {
                    let user = try await localRepository.getUid()
                    let result =  try await loginProvider.deleteAccount()
                    if (result.isSuccess) {
                        try await localRepository.removeUid()
                        try await localRepository.removeTapaVoted()
                        try await firestoreProvider.removeVote(user: user)
                        print("########## deleteAccount Result OK OK OK")
                        self.state = MainStateSwift(logout: true)
                    }
                } catch {
                    print("Unable to delete account")
                    self.state = MainStateSwift(error: "RemoteConfigError.LogoutFailed")
                }
            }
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
    }
}

