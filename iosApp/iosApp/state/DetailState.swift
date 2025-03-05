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
    var logout: Bool = false
    var tapa: TapaItemBo? = nil
    var location: TapaLocation? = nil
    var hasLocationPermission: Bool? = nil
    var isGPSActive: Bool = false
    var isInRadius: Bool? = nil
    var voteStatus: VoteStatus = VoteStatus.unknown
}


