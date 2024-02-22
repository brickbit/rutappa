//
//  DetailState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum DetailStateSwift {
    case loading
    case loaded(tapa: TapaItemBo, voted: Bool)
    case voted(tapa: TapaItemBo, voted: Bool)
}

extension DetailStateSwift {
    init?(_ value: DetailState) {
        switch value {
        case is DetailState.Loading:
            self = .loading
        case let loaded as DetailState.Loaded:
            self = .loaded(tapa: loaded.tapa, voted: loaded.voted)
        case let votedTapa as DetailState.Voted:
            self = .voted(tapa: votedTapa.tapa, voted: votedTapa.voted)
        default:
            return nil
        }
    }
}


