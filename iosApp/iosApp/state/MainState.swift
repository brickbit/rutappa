//
//  MainState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum MainStateSwift {
    case loading
    case loaded(tapas: [TapaItemBo])
    case logout
    case error
}

extension MainStateSwift {
    init?(_ value: MainState) {
        switch value {
        case is MainState.Loading:
            self = .loading
        case let loaded as MainState.Loaded:
            self = .loaded(tapas: loaded.tapas)
        case is MainState.Logout:
            self = .logout
        default:
            self = .error
        }
    }
}

