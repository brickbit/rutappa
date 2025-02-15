//
//  SplashView.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI
import shared

struct SplashView: View {
    @ObservedObject var viewModel: IOSSplashViewModel
    @EnvironmentObject var navigator: Navigator

    init() {
        self.viewModel = IOSSplashViewModel()
    }
    
    var body: some View {
        VStack {
            splashContent()
        }
        .onAppear {
            viewModel.startObserving()
        }
        .onDisappear {
            viewModel.dispose()
        }
    }
    
    func splashContent() -> AnyView {
        switch viewModel.state.route {
        case .splash: return AnyView(SplashScreen())
        case .login:
            Task{
                navigator.navigate(to: .login)
            }
            return AnyView(SplashScreen())
        case .main:
            Task{
                navigator.navigate(to: .main)
            }
            return AnyView(SplashScreen())
        case .detail(tapaId: let tapaId):
            Task{
                navigator.navigate(to: .detail(tapaId: tapaId))
            }
            return AnyView(SplashScreen())
        case .partners:
            Task{
                navigator.navigate(to: .partners)
            }
            return AnyView(SplashScreen())
        }
    }
}

struct SplashScreen: View {
    var body: some View {
        ZStack {
            Image(.beansBackground)
                .resizable()
                .scaledToFill()
                .frame(maxWidth: .infinity, maxHeight: .infinity)
                .edgesIgnoringSafeArea(.all)
            Rectangle().fill(
                LinearGradient(gradient: Gradient(colors: [Color("secondaryColor").opacity(0.7), Color("secondaryColor").opacity(0.85), Color("secondaryColor").opacity(1)]), startPoint: .top, endPoint: .bottom)
            ).frame(maxWidth: .infinity, maxHeight: .infinity)
                .edgesIgnoringSafeArea(.all)
            VStack(alignment: .center) {
                Image(.rutappaSplash)
                    .resizable()
                    .frame(width: 450, height: 450)
                    .padding()
                Image(.legumbresTapas)
                    .resizable()
                    .frame(width: 250, height: 20)
                Text("©2025 desTAPA las LEGUMBRES")
                    .foregroundStyle(Color("primaryColor"))
                    .font(Font.custom("Montserrat", size: 16))
                Image(.tierraSaborLogo)
                    .resizable()
                    .frame(width: 110, height: 110)

            }
        }
    }
}

struct SplashView_Previews: PreviewProvider {
    static var previews: some View {
        SplashView()
    }
}

extension SplashView {
    @MainActor class IOSSplashViewModel: ObservableObject {
        private let viewModel: SplashViewModel
                
        @Published var state: SplashStateSwift = SplashStateSwift.initialized
        
        private var handle: DisposableHandle?

        init() {
            self.viewModel = SplashViewModel.shared
        }
        
        // Observes to state changes
        func startObserving() {
            handle = viewModel.state.subscribe(onCollect: { state in
                if let state = state {
                    self.state = SplashStateSwift(state) ?? .initialized
                }
            })
        }
        
        // Removes the listener
        func dispose() {
            handle?.dispose()
        }
    }
}

