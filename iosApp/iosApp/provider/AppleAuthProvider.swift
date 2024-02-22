//
//  AppleAuthProvider.swift
//  iosApp
//
//  Created by Roberto García Romero on 11/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import _AuthenticationServices_SwiftUI

class AppleAuthProvider {
    static let shared = AppleAuthProvider()
    
    private init() {}
    
    private var request: ASAuthorizationAppleIDRequest? = nil

    func setRequest(request: ASAuthorizationAppleIDRequest) {
        self.request = request
    }
    
    func getRequest() -> ASAuthorizationAppleIDRequest? {
        return self.request
    }
}
