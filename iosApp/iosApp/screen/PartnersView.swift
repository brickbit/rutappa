//
//  PartnersView.swift
//  iosApp
//
//  Created by Roberto on 8/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct PartnersView: View {
    @ObservedObject var viewModel: IOSPartnersViewModel
    @EnvironmentObject var navigator: Navigator

    init() {
        self.viewModel = IOSPartnersViewModel()
    }
    
    var body: some View {
        VStack {
            partnersContent()
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func partnersContent() -> AnyView {
        return AnyView(
            PartnersScreen(
                logoutAction: {
                    viewModel.logout()
                },
                navigateToTapas: {
                    Task {
                        navigator.navigate(to: .main)
                    }
                },
                deleteAccountAction: {
                    Task {
                        viewModel.deleteAccount()
                    }
                }
            )
        )
    }
}

struct PartnersScreen: View {
    let logoutAction: () -> ()
    let navigateToTapas: () -> ()
    let deleteAccountAction: () -> ()
    @State private var showingLogout = false
    @State private var showingMenu = false
    
    var body: some View {
        ZStack {
            ZStack(alignment: .bottom) {
                ZStack(alignment: .top) {
                    ZStack {
                        GradientBackground()
                        PartnersContent()
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
                        navigateToTapas()
                    },
                    onPartnersClicked: {
                        showingMenu.toggle()
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

struct PartnersView_Previews: PreviewProvider {
    static var previews: some View {
        PartnersView()
    }
}

extension PartnersView {
    @MainActor class IOSPartnersViewModel: ObservableObject {
        private let viewModel: PartnerViewModel
        private let localRepository: LocalRepository = LocalRepositoryImpl.shared
        private let loginProvider: LoginProvider = LoginProvideImpl.shared
        private let firestoreProvider: FirestoreProvider = FirestoreProviderImpl.shared
        
        @Published var state: PartnersStateSwift = PartnersStateSwift()
        
        private var handle: DisposableHandle?

        init() {
            self.viewModel = PartnerViewModel.shared
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = PartnersStateSwift(
                        isLoading: state.isLoading,
                        error: state.error,
                        logout: state.logout
                    )
                }
            })
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
        
        func logout() {
            viewModel.logout()
            
        }
        
        func resetState() {
            self.state = PartnersStateSwift()
            print("########## resetState \(self.state)")
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
                        self.state = PartnersStateSwift(logout: true)
                    }
                } catch {
                    print("Unable to delete account")
                }
            }
        }
    }
}


struct PartnersContent: View {
    var body: some View {
        ScrollView {
            Spacer(minLength: 140)
            VStack(alignment: .center) {
                VStack(alignment: .center) {
                    Text("Gracias a todos aquellos que han hecho posible que el Campeonato desTAPA las LEGUMBRES Tierra de Sabor sea hoy una realidad.")
                        .multilineTextAlignment(.center)
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Montserrat", size: 14))
                        .padding(.top, 32)
                    Text("Ellos han confiado en esta idea, y han contribuido a dar a las legumbres el lugar que se merecen.")
                        .multilineTextAlignment(.center)
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Montserrat", size: 14))
                        .padding(.top, 8)
                    Text("COLABORADOR PRINCIPAL")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                        .padding(.top, 16)
                    Image(.tierraSaborBlack)
                        .resizable()
                        .frame(width: 220, height: 220)
                }.padding(.horizontal, 20)
                VStack(alignment: .center, spacing: 16) {
                    Text("PATROCINADORES")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                        .padding(.top, 16)
                    Image(.cajaRural)
                        .resizable()
                        .frame(width: 200, height: 95)
                    Image(.fertinagro)
                        .resizable()
                        .frame(width: 200, height: 85)
                    AsyncImage(url: URL(string: "https://www.agrorenedo.com/wp-content/uploads/2020/08/Triticum-agro-1.png"),
                               content: { image in
                        image.resizable()
                            .aspectRatio(contentMode: .fill)
                            .frame(maxWidth: 200, maxHeight: 90)
                    },
                               placeholder: {
                        ProgressView()
                            .frame(maxWidth: 200, maxHeight: 90)
                    }
                    )
                    Image(.vizarLogo)
                        .resizable()
                        .frame(width: 200, height: 70)
                    Text("ORGANIZACIÓN TÉCNICA")
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Berlin Sans FB Demi", size: 20))
                        .padding(.top, 16)
                    Image(.gastronomicon)
                        .resizable()
                        .frame(width: 180, height: 120)
                    Spacer(minLength: 100)
                }.frame(maxWidth: .infinity).background(Color("partnerBackground"))
            }
        }
    }
}
