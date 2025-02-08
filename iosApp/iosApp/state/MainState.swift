//
//  MainState.swift
//  iosApp
//
//  Created by Roberto García Romero on 5/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

struct MainStateSwift {
    var isLoading: Bool = false
    var tapas: [TapaItemBo] = []
    var filteredTapas: [TapaItemBo] = []
    var logout: Bool = false
    var provinces: [String] = []
    var error: String? = nil
}

