//
//  DetailState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

struct DetailStateSwift {
    var isLoading: Bool = true
    var tapa: TapaItemBo? = nil
    var voted: Bool = false
    var location: KotlinPair<NSString, NSString>? = nil
    var canVote: Bool = false
    var isWithinRadius: Bool = false
}


