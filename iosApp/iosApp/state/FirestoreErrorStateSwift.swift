//
//  FirestoreErrorSwift.swift
//  iosApp
//
//  Created by Roberto García Romero on 14/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared

enum FirestoreErrorStateSwift {
    case noError
    case tapaVotedYet
    case writtingError
    case redingError
    case noUserError
}

extension FirestoreErrorStateSwift {
    init?(_ value: FirestoreError) {
        switch value {
        case is FirestoreError.NoError:
            self = .noError
        case is FirestoreError.TapaVotedYet:
            self = .tapaVotedYet
        case is FirestoreError.WritingError:
            self = .writtingError
        case is FirestoreError.ReadingError:
            self = .redingError
        case is FirestoreError.NoUserError:
            self = .noUserError
        default:
            return nil
        }
    }
}
