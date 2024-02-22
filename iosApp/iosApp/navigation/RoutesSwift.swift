//
//  RoutesSwift.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum RouteSwift: Hashable {
    case splash
    case login
    case main
    case detail(tapaId: String)
}

extension RouteSwift {
    init?(_ value: Routes) {
        switch value {
        case let splash as Routes.Splash:
            self = .splash
        case let login as Routes.Login:
            self = .login
        case let mainRoute as Routes.Main:
            self = .main
        case let detailRoute as Routes.Detail:
            self = .detail(tapaId: "")
        default:
            return nil
        }
    }
}
