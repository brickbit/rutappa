//
//  ResultAuthProvider.swift
//  iosApp
//
//  Created by Roberto García Romero on 11/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import FirebaseAuth
import _AuthenticationServices_SwiftUI

class ResultAuthProvider {
    static let shared = ResultAuthProvider()
    
    private init() {}
    
    private var result: Result<ASAuthorization,Error>? = nil

    func setResult(result: Result<ASAuthorization,Error>) {
        self.result = result
    }
    
    func getResult() -> Result<ASAuthorization,Error>? {
        return self.result
    }
}
