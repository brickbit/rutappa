//
//  SplashState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum SplashStateSwift {
    case initialized
    case finished(route: RouteSwift)
}

extension SplashStateSwift {
    init?(_ value: SplashState) {
        switch value {
        case is SplashState.Init:
            self = .initialized
        case let finished as SplashState.Finished:
            self = .finished(route: RouteSwift(finished.route) ?? RouteSwift.login)
        default:
            return nil
        }
    }
}
