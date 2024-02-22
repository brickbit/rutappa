//
//  TapaItemSwift.swift
//  iosApp
//
//  Created by Roberto García Romero on 10/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

struct TapaITemSwift: Codable {
    let id: String
    let name: String
    let photo: String
    let shortDescription: String
    let legumes: [String]
    let local:LocalItemSwift
}

extension TapaITemSwift {
    func toBo() -> TapaItemBo {
        return TapaItemBo(id: id, name: name, photo: photo, shortDescription: shortDescription, legumes: legumes, local: local.toBo())
    }
}
