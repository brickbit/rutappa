import SwiftUI
import shared
import FirebaseCore

@main
struct iOSApp: App {
    @UIApplicationDelegateAdaptor(AppDelegate.self) var delegate
    @ObservedObject var navigator = Navigator()
    
    init() {
        HelperKt.doInitKoin()
    }
    
    var body: some Scene {
        WindowGroup {
            NavigationStack(path: $navigator.navPath) {
                SplashView()
                    .navigationDestination(for: RouteSwift.self) { destination in
                        switch destination {
                        case .splash:
                            SplashView()
                        case .login:
                            LoginView().navigationBarBackButtonHidden(true)
                        case .main:
                            MainView().navigationBarBackButtonHidden(true)
                        case .detail(let id):
                            DetailView(id: id)
                        case .partners:
                            PartnersView()
                        }
                    }
            }.accentColor(.white)
            .navigationBarHidden(true)
            .toolbar(.hidden, for: .navigationBar)
            .environmentObject(navigator)
        }
    }
}

