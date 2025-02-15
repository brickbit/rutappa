//
//  LocalItemSwift.swift
//  iosApp
//
//  Created by Roberto García Romero on 10/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

struct LocalItemSwift: Codable {
    let id: String
    let name: String
    let province: String
    let instagram: String
    let facebook: String
    let longitude: String
    let latitude: String
}


extension LocalItemSwift {
    func toBo() -> LocalItemBo {
        return LocalItemBo(id: id, name: name, province: province, instagram: instagram, facebook: facebook, longitude: longitude, latitude: latitude )
    }
}
