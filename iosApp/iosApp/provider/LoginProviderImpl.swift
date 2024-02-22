//
//  LoginProviderImpl.swift
//  iosApp
//
//  Created by Roberto García Romero on 11/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import Foundation
import shared
import FirebaseAuth
import _AuthenticationServices_SwiftUI
import CryptoKit

class LoginProvideImpl: LoginProvider {
    
    static let shared = LoginProvideImpl()
    
    private var currentNonce: String? = nil
    
    private var appleAuthProvider: AppleAuthProvider = AppleAuthProvider.shared
    private var resultAuthtProvider: ResultAuthProvider = ResultAuthProvider.shared
    
    private init() {}
    
    func logout(completionHandler: @escaping (KmmresultKmmResult<KotlinUnit>?, Error?) -> Void) {
        do {
            try Auth.auth().signOut()
            completionHandler(KmmresultKmmResult(value: KotlinUnit()), nil)
        } catch {
            print("Failed to deleteCurrentUser")
        }
    }
    
    func deleteAccount(completionHandler: @escaping (KmmresultKmmResult<KotlinUnit>?, Error?) -> Void) {
        Task {
            guard let user = Auth.auth().currentUser else { return }
            guard let lastSignInDate = user.metadata.lastSignInDate else { return }
            let needsReauth = !lastSignInDate.isWithinPast(minutes: 5)
            let needsTokenRevocation = user.providerData.contains{ $0.providerID == "apple.com"}
            do {
                if(needsReauth || needsTokenRevocation == true) {
                    let signInWithApple = SignInWithApple()
                    let appleIdCredential = try await signInWithApple()
                    
                    guard let appleIdToken = appleIdCredential.identityToken else {
                        print("Unable to fetch identity token")
                        return
                    }
                    
                    guard let idTokenString = String(data: appleIdToken, encoding: .utf8) else {
                        print("Unable to serialize token stringfrom data")
                        return
                    }
                    
                    let nonce = randomNonceString()
                    let credential = OAuthProvider.credential(withProviderID: "apple.com",
                                                              idToken: idTokenString,
                                                              rawNonce: nonce)                
                    if (needsReauth) {
                        try await user.reauthenticate(with: credential)
                    }
                    if(needsTokenRevocation == true) {
                        guard let authorizationCode = appleIdCredential.authorizationCode else { return }
                        guard let authCodeString = String(data: authorizationCode, encoding: .utf8) else { return }
                        
                        try await Auth.auth().revokeToken(withAuthorizationCode: authCodeString)
                    }
                }
                try await user.delete()
                completionHandler(KmmresultKmmResult(value: KotlinUnit()), nil)                
            } catch {
                print("Unable to delete account")
            }
        }
    }

    func getSignedInUser() -> String? {
    
        return Auth.auth().currentUser?.uid
    }
    
    func signIn(completionHandler: @escaping (KmmresultKmmResult<KotlinUnit>?, Error?) -> Void) {
        appleAuthProvider.getRequest()?.requestedScopes = [.email]
        let nonce = randomNonceString()
        currentNonce = nonce
        appleAuthProvider.getRequest()?.nonce = sha256(nonce)
        completionHandler(KmmresultKmmResult(value: KotlinUnit()), nil)
    }

    
    func signInWithIntent(completionHandler: @escaping (KmmresultKmmResult<NSString>?, Error?) -> Void) {
        if case .failure(let failure) = resultAuthtProvider.getResult() {
            //errorMessage = failure.localizedDescription
        } else if case .success(let success) = resultAuthtProvider.getResult() {
            if let appleIdCredential = success.credential as? ASAuthorizationAppleIDCredential {
                guard let nonce = currentNonce else {
                    fatalError("Invalid state: a login callback was received, but no login request was set")
                }
                guard let appleIDToken = appleIdCredential.identityToken else {
                    print("Unable to fetch identity token")
                    return
                }
                guard let idTokenString = String(data: appleIDToken, encoding: .utf8) else {
                    print("Unable to serialize token string from data: \(appleIDToken.debugDescription)")
                    return
                }
                
                let credential = OAuthProvider.credential(withProviderID: "apple.com", idToken: idTokenString, rawNonce: nonce)
                Task {
                    do {
                        let result = try await Auth.auth().signIn(with: credential)
                        completionHandler(KmmresultKmmResult(value: result.user.uid as NSString?), nil)
                    }
                    catch {
                        print("Error authenticating: \(error.localizedDescription)")
                    }
                }
            }
        }
    }
    
    
    private func randomNonceString(length: Int = 32) -> String {
      precondition(length > 0)
      var randomBytes = [UInt8](repeating: 0, count: length)
      let errorCode = SecRandomCopyBytes(kSecRandomDefault, randomBytes.count, &randomBytes)
      if errorCode != errSecSuccess {
        fatalError(
          "Unable to generate nonce. SecRandomCopyBytes failed with OSStatus \(errorCode)"
        )
      }

      let charset: [Character] =
        Array("0123456789ABCDEFGHIJKLMNOPQRSTUVXYZabcdefghijklmnopqrstuvwxyz-._")

      let nonce = randomBytes.map { byte in
        // Pick a random character from the set, wrapping around if needed.
        charset[Int(byte) % charset.count]
      }

      return String(nonce)
    }
    
    private func sha256(_ input: String) -> String {
      let inputData = Data(input.utf8)
      let hashedData = SHA256.hash(data: inputData)
      let hashString = hashedData.compactMap {
        String(format: "%02x", $0)
      }.joined()

      return hashString
    }
    
}
extension Date {
  func isWithinPast(minutes: Int) -> Bool {
    let now = Date.now
    let timeAgo = Date.now.addingTimeInterval(-1 * TimeInterval(60 * minutes))
    let range = timeAgo...now
    return range.contains(self)
  }
}

class SignInWithApple: NSObject, ASAuthorizationControllerDelegate {

  private var continuation : CheckedContinuation<ASAuthorizationAppleIDCredential, Error>?

  func callAsFunction() async throws -> ASAuthorizationAppleIDCredential {
    return try await withCheckedThrowingContinuation { continuation in
      self.continuation = continuation
      let appleIDProvider = ASAuthorizationAppleIDProvider()
      let request = appleIDProvider.createRequest()
      request.requestedScopes = [.email]

      let authorizationController = ASAuthorizationController(authorizationRequests: [request])
      authorizationController.delegate = self
      authorizationController.performRequests()
    }
  }

  func authorizationController(controller: ASAuthorizationController, didCompleteWithAuthorization authorization: ASAuthorization) {
    if case let appleIDCredential as ASAuthorizationAppleIDCredential = authorization.credential {
      continuation?.resume(returning: appleIDCredential)
    }
  }

  func authorizationController(controller: ASAuthorizationController, didCompleteWithError error: Error) {
    continuation?.resume(throwing: error)
  }
}
