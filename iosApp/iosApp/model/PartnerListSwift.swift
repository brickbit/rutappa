//
//  PartnerListSwift.swift
//  iosApp
//
//  Created by Roberto García Romero on 20/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared

struct PartnerListSwift: Codable {
    let categories: [CategoryPartnerSwift]
}

struct CategoryPartnerSwift: Codable {
    let name: String
    let background: String
    let partners: [PartnerSwift]
}

struct PartnerSwift: Codable {
    let image: String
    let link: String
}


extension PartnerListSwift {
    func toBo() -> PartnersListBO {
        return PartnersListBO(categories: categories.map{ $0.toBo()})
    }
}

extension CategoryPartnerSwift {
    func toBo() -> CategoryPartnerBO {
        return CategoryPartnerBO(name: name, background: background, partners: partners.map{ $0.toBO()})
    }
}

extension PartnerSwift {
    func toBO() -> PartnerBO {
        return PartnerBO(image: image, link: link)
    }
}
