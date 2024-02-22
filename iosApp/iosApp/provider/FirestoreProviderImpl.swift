//
//  FirestoreProviderImpl.swift
//  iosApp
//
//  Created by Roberto García Romero on 11/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import FirebaseFirestore

class FirestoreProviderImpl: FirestoreProvider {
    
    
    static let shared = FirestoreProviderImpl()

    private init() {
        print("Firestore initialized")
    }

    let db = Firestore.firestore()

    func vote(user: String, vote: Int32, tapa: String, completionHandler: @escaping (KmmresultKmmResult<KotlinUnit>?, Error?) -> Void) {
        Task {
            var votedYet = false
            do {
                let snapshot = try await db.collection("tapa_contest").getDocuments()
                for document in snapshot.documents {
                    print("\(document.documentID) => \(document.data())")
                    let dbUser: String? = document.data()["user"] as? String
                    let dbTapa: String? = document.data()["tapa"] as? String
                    if(dbUser == user && dbTapa == tapa) {
                        votedYet = true
                        completionHandler(nil, FirestoreError.TapaVotedYet() as? Error)
                    }
                }
                if(!votedYet) {
                    do {
                        let ref = try await db.collection("tapa_contest").addDocument(data: [
                            "user": user,
                            "vote": vote,
                            "tapa": tapa
                        ])
                        
                        print("Document added with ID: \(ref.documentID)")
                        completionHandler(KmmresultKmmResult(value: KotlinUnit()), nil)
                    } catch {
                        print("Error adding document: \(error)")
                        completionHandler(KmmresultKmmResult(failure: FirestoreError.WritingError()), nil)
                    }
                }
            } catch {
                print("Error getting documents: \(error)")
                completionHandler(nil, FirestoreError.ReadingError() as? Error)
            }
        }
    }
    
    func removeVote(user: String, completionHandler: @escaping (KmmresultKmmResult<KotlinUnit>?, Error?) -> Void) {
        Task {
            do {
                let snapshot = try await db.collection("tapa_contest").getDocuments()
                for document in snapshot.documents {
                    let dbUser: String? = document.data()["user"] as? String
                    if(dbUser == user) {
                        do {
                            try await db.collection("tapa_contest").document(document.documentID).delete()
                        } catch {
                            print("Error deleting documents: \(error)")
                            completionHandler(nil, FirestoreError.ReadingError() as? Error)
                        }
                    }
                    completionHandler(KmmresultKmmResult(value: KotlinUnit()), nil)
                }
            } catch {
                print("Error getting documents: \(error)")
                completionHandler(nil, FirestoreError.ReadingError() as? Error)
            }
        }
    }
}
