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
import Kingfisher

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
                    provinces: viewModel.state.provinces,
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
                    }, onFilterTapa: { filter in
                        Task {
                            viewModel.filterTapa(filter: filter)
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
    let provinces: [String]
    let action: (String) -> ()
    let logoutAction: () -> ()
    let deleteAccountAction: () -> ()
    let navigateToPartnersAction: () -> ()
    let onFilterTapa: (String) -> ()
    var logout = UIImage(named: "logout")
    
    @State private var showingLogout = false
    @State private var showingMenu = false

    var body: some View {
        ZStack {
            ZStack(alignment: .bottom) {
                ZStack(alignment: .top) {
                    ZStack {
                        GradientBackground()
                        TapaListScrollable(
                            action: action,
                            onFilterTapa: onFilterTapa,
                            tapas: tapas,
                            provinces: provinces
                        )
                    }
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
                SocialWallView()
            }
            if(showingMenu) {
                MenuRutapa(
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
                KFImage(URL(string: tapa.photo))
                    .cacheOriginalImage()
                    .placeholder {
                        ProgressView()
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(maxWidth: 120, maxHeight: 120)
                    
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
        
        func filterTapa(filter: String) {
            viewModel.filterTapa(filter: filter)
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
    }
}


struct TapaListScrollable: View {
    var action: (String) -> ()
    var onFilterTapa: (String) -> ()
    var tapas: [TapaItemBo]
    var provinces: [String]

    @State private var selection = "Todas"

    var body: some View {
        ScrollView {
            Spacer(minLength: 140)
            VStack(alignment: .leading) {
                Text("LAS TAPAS")
                    .foregroundStyle(Color("secondaryColor"))
                    .font(Font.custom("Berlin Sans FB Demi", size: 18))
                    .padding(.horizontal,24)
                    .padding(.top, 16)
                ZStack(alignment: .topLeading) {
                    HStack {
                        Spacer()
                        Picker("Filtrar por provincia", selection: $selection) {
                            ForEach(provinces, id: \.self) { option in
                                Text(option)
                                    .font(.headline)
                                    .padding()
                                    .background(RoundedRectangle(cornerRadius: 8).fill(Color.gray.opacity(0.2)))
                                    .foregroundColor(.black)
                            }
                        }
                        .onChange(of: selection) { oldValue, newValue in
                            onFilterTapa(newValue)
                        }
                        .pickerStyle(.menu)
                        .accentColor(Color(.dropdownForeground))
                    }
                    
                    Text("Filtrar por provincia")
                        .font(Font.custom("Montserrat", size: 12))
                        .foregroundColor(Color(.dropdownForeground))
                        .padding(.top, 4)
                        .padding(.leading, 8)

                }
                .frame(maxWidth: .infinity)
                .frame(height: 44)
                .background(Color(.dropdownBackground))
                .cornerRadius(12)
                .padding(.horizontal, 24)
                .padding(.bottom, 8)


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
            Spacer(minLength: 120)
        }
    }
}
