//
//  LoginState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum LoginStateSwift {
    case notLogged
    case loading
    case logged(mail: String)
}

extension LoginStateSwift {
    init?(_ value: LoginState) {
        switch value {
        case is LoginState.NotLogged:
            self = .notLogged
        case is LoginState.Loading:
            self = .loading
        case let loaded as LoginState.Logged:
            self = .logged(mail: "")
        default:
            return nil
        }
    }
}

