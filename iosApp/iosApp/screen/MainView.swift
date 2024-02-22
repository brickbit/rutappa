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
    init() {
        self.viewModel = IOSMainViewModel()
    }
    
    var body: some View {
        VStack {
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
        
        switch viewModel.state {
        case .loading: return AnyView(LoadingView())
        case .loaded(let tapas):
            return AnyView(
                MainScreen(
                    tapas: tapas,
                    action: { tapa in
                        Task {
                            navigator.navigate(to: .detail(tapaId: tapa))
                        }
                    },
                    logoutAction:  {
                        Task {
                            viewModel.logout()
                        }
                    },
                    deleteAccountAction:  {
                        Task {
                            viewModel.deleteAccount()
                        }
                    }
                ).overlay(alignment: .top) {
                    Color("secondaryColor")
                        .background(Color("secondaryColor"))
                        .ignoresSafeArea(edges: .top)
                        .frame(height: 0)
                }
            )
        case .logout:
            Task{
                if(!navigated) {
                    navigated = true
                    navigator.navigate(to: .login)
                }
            }
            return AnyView(LoadingView())
        case .error:
            Task{
                if(!navigated) {
                    navigated = true
                    navigator.navigate(to: .login)
                }
            }
            return AnyView(LoadingView())
        }
    
    }
}


struct MainScreen: View {
    let tapas: [TapaItemBo]
    let action: (String) -> ()
    let logoutAction: () -> ()
    let deleteAccountAction: () -> ()
    var logout = UIImage(named: "logout")
    @State private var showingLogout = false
    
    var body: some View {
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
                                    hasLogout: true, onItemClicked: {
                                        showingLogout.toggle()
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
    }
}

struct TapaItemList: View {
    let tapa: TapaItemBo
    var body: some View {
        VStack(alignment: .leading) {
            HStack(alignment: .center) {
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
                        .frame(width: 200)
                        .foregroundStyle(Color("secondaryColor"))
                        .font(Font.custom("Montserrat", size: 16))
                    HStack {
                        Image("ic_location")
                            .resizable()
                            .frame(width: 16, height: 16)
                        Text("\(tapa.local.name) \(tapa.local.province)")
                            .foregroundStyle(Color("secondaryColor"))
                            .font(Font.custom("Montserrat", size: 12))

                    }                        
                    .frame(width: 200)

                    Text(tapa.shortDescription)
                        .frame(width: 200)
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
                
        @Published var state: MainStateSwift = MainStateSwift.loading
        
        private var handle: DisposableHandle?

        init() {
            self.viewModel = MainViewModel.shared
            self.viewModel.getListTapas(configuration: 0)
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = MainStateSwift(state) ?? .loading
                }
            })
        }
        
        func logout() {
            viewModel.logout()
        }
        
        func deleteAccount() {
            viewModel.deleteAccount()
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
    }
}

