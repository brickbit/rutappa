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
import Kingfisher

struct PartnersView: View {
    @ObservedObject var viewModel: IOSPartnersViewModel
    @EnvironmentObject var navigator: Navigator

    init() {
        self.viewModel = IOSPartnersViewModel()
    }
    
    var body: some View {
        return AnyView(partnersContent())
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func partnersContent() -> AnyView {
        @State var shouldNavigateLogout = viewModel.state.logout

        return AnyView(
            PartnersScreen(
                partners: viewModel.state.partners,
                logoutAction: {
                    Task {
                        viewModel.logout()
                        navigator.logout()
                    }
                },
                navigateToTapas: {
                    Task {
                        navigator.navigate(to: .main)
                    }
                },
                deleteAccountAction: {
                    Task {
                        viewModel.deleteAccount()
                        navigator.logout()
                    }
                }
            )
            .overlay(alignment: .top) {
                Color("secondaryColor")
                    .background(Color("secondaryColor"))
                    .ignoresSafeArea(edges: .top)
                    .frame(height: 0)
                    
            }
        )
    }
}

struct PartnersScreen: View {
    var partners: PartnersListBO?
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
                        PartnersContent(
                            partners: partners
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
            self.viewModel.getPartners(configuration: 0)
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = PartnersStateSwift(
                        isLoading: state.isLoading,
                        error: state.error,
                        logout: state.logout,
                        partners: state.partners
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
    var partners:  PartnersListBO?
    var body: some View {
        ScrollView {
            Spacer(minLength: 140)
            VStack(alignment: .center, spacing: 0) {
                VStack(alignment: .center, spacing: 0) {
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
                    if let partners = partners {
                        var lastindex = partners.categories.count - 1
                        ForEach(partners.categories, id: \.self) { category in
                            var last = category == partners.categories[lastindex]
                            CategoryItemView(
                                category: category,
                                first: category == partners.categories[0],
                                last: last
                            )
                        }
                    } else {
                        EmptyView()
                    }
                    
                }
               
            }
        }
    }
}


struct CategoryItemView: View {
    var category: CategoryPartnerBO
    let first: Bool
    let last: Bool
    var body: some View {
        var color: Color = Color(UIColor(oxHex: "0x\(category.background)") ?? UIColor.blue)
        var padding: CGFloat = last ? 100 : first ? 24 : 0
        VStack (spacing: 0){
            Text(category.name)
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Berlin Sans FB Demi", size: 20))
                .padding(.top, 16)
            ForEach(category.partners, id: \.self) { partner in
                KFImage(URL(string: partner.image))
                    .cacheOriginalImage()
                    .placeholder {
                        ProgressView()
                    }
                    .resizable()
                    .aspectRatio(contentMode: .fill)
                    .frame(maxWidth: .infinity)
                    .padding(.vertical, 8)
                    .onTapGesture {
                        guard let url = URL(string: partner.link) else { return }
                        UIApplication.shared.open(url)
                    }
            }
        }
        .padding(.horizontal,16)
        .padding(.bottom, padding)
        .background(category.background == "000000" ? color.opacity(0.0) : color)
    }
}

extension UIColor {
    convenience init?(oxHex: String) {
        // Ensure the string starts with "OX" and is 8 characters long
        guard oxHex.hasPrefix("0x"), oxHex.count == 8 else { return nil }

        // Remove "OX" prefix
        let hexString = String(oxHex.dropFirst(2))

        // Convert hex to integer
        var hexInt: UInt64 = 0
        Scanner(string: hexString).scanHexInt64(&hexInt)

        // Extract RGB components
        let red = CGFloat((hexInt >> 16) & 0xFF) / 255.0
        let green = CGFloat((hexInt >> 8) & 0xFF) / 255.0
        let blue = CGFloat(hexInt & 0xFF) / 255.0

        // Initialize UIColor
        self.init(red: red, green: green, blue: blue, alpha: 1.0)
    }
}
