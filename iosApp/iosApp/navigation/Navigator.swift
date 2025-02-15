//
//  Navigator.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import SwiftUI

final class Navigator: ObservableObject {
    
    @Published var navPath = NavigationPath()
    private var routeHistory: [RouteSwift] = [] // Track visited routes

    func navigate(to route: RouteSwift) {
        navPath.append(route)
        routeHistory.append(route)
    }
    
    func navigateBack() {
        if !navPath.isEmpty {
            navPath.removeLast()
            routeHistory.removeLast()
        }
    }
    
    func navigateToRoot() {
        navPath = NavigationPath()
        routeHistory.removeAll()
    }
    
    func logoutAndNavigate(to route: RouteSwift) {
        navPath = NavigationPath() // Reset stack
        routeHistory.removeAll()
        DispatchQueue.main.async {
            self.navPath.append(route) // Navigate to the login or desired screen
            self.routeHistory.append(route)
        }
    }
}
