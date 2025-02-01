//
//  LocationProviderImpl.swift
//  iosApp
//
//  Created by Roberto on 1/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared

class LocationProviderImpl: LocationProvider {
    func getLocation() -> ResultKMM<KotlinPair<NSString, NSString>> {
        return ResultKMMSuccess(data: KotlinPair(first: "", second: ""))
    }
    
    func hasPermission() -> Bool {
        return false
    }
    
    func requestPermission() {
        
    }
    
    
    
}
