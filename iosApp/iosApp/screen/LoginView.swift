//
//  LoginView.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared
import FirebaseAuth
import _AuthenticationServices_SwiftUI

struct LoginView: View {
    @ObservedObject var viewModel: IOSLoginViewModel
    @EnvironmentObject var navigator: Navigator

    init() {
        self.viewModel = IOSLoginViewModel()
    }
    var body: some View {
        VStack {
            loginContent()
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func loginContent() -> AnyView {
        if(viewModel.state.isLoading) {
            return AnyView(
                LoginScreen(
                    signIn: {},
                    logIn: {}
                )
            )
        } else {
            return AnyView(
                LoginScreen(
                    signIn: {
                        Task {
                            viewModel.signIn()
                        }
                    },
                    logIn: {
                        Task {
                            viewModel.signInWithIntent()
                            navigator.navigate(to: .main)
                        }
                    }
                )
            )
        }
    }
}


struct LoginScreen: View {
    let signIn: () -> ()
    let logIn: () -> ()

    var body: some View {
        ZStack(alignment: .bottom) {
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
                ScrollView {
                    VStack {
                        LoginInformation(
                            signIn: { signIn() },
                            logIn: { logIn() }
                        )
                    }.padding(.bottom, 120)
                }.safeAreaInset(edge: .bottom) {
                    VStack{
                        SocialWallView()
                    }
                }
            }.edgesIgnoringSafeArea(.bottom)
        }
        .edgesIgnoringSafeArea(.bottom)
    }
}

struct LoginInformation: View {
    let signIn: () -> ()
    let logIn: () -> ()
    
    var body: some View {
        VStack {
            Image(.destapa)
                .resizable()
                .frame(width: 360, height: 180)
                .padding()
            Text("II CAMPEONATO NACIONAL desTAPA las LEGUMBRES")
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Berlin Sans FB Demi", size: 20))
                .multilineTextAlignment(.center)
                .padding(.horizontal,24)
                .padding(.bottom, 8)
                .padding(.horizontal, 24)
            Text("BIENVENIDO AL CAMPEONATO NACIONAL desTAPA las LEGUMBRES TIERRA DE SABOR.")
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Montserrat", size: 16))
                .multilineTextAlignment(.center)
                .fixedSize(horizontal: false, vertical: true)
                .padding(.horizontal, 24)
            Text("Puedes consultar el reglamento del Campeonato en este enlace")
                .foregroundStyle(Color("secondaryColor"))
                .font(Font.custom("Montserrat", size: 14))
                .multilineTextAlignment(.center)
                .padding(.vertical, 16)
                .onTapGesture {
                    if let url = URL(string: "https://destapalaslegumbres.es/"), UIApplication.shared.canOpenURL(url) {
                        UIApplication.shared.open(url)
                    }
                }.padding(.horizontal, 24)
            SignInWithAppleButton(onRequest: { request in
                AppleAuthProvider.shared.setRequest(request: request)
                signIn()
            }, onCompletion: { result in
                ResultAuthProvider.shared.setResult(result: result)
                logIn()
            })
            .frame(maxWidth: .infinity)
            .frame(height: 50)
            .cornerRadius(25)
            .padding(.horizontal, 24)
        }
    }
}

struct LoginView_Previews: PreviewProvider {
    static var previews: some View {
        LoginView()
    }
}

extension LoginView {
    @MainActor class IOSLoginViewModel: ObservableObject {
            
        private let viewModel: LoginViewModel
                
        @Published var state: LoginStateSwift = LoginStateSwift()
        
        private var handle: DisposableHandle?

        init() {
            self.viewModel = LoginViewModel.shared
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = LoginStateSwift(isLoading: state.isLoading, logged: state.logged, mail: state.mail)
                }
            })
        }
        
        func signIn() {
            viewModel.signIn()
        }
        
        func signInWithIntent() {
            viewModel.signInWithIntent()
        }
        
        // Removes the listener
        func dispose() {
            self.state = LoginStateSwift(isLoading: state.isLoading, logged: false, mail: state.mail)
            handle?.dispose()
        }
    
    }
}
