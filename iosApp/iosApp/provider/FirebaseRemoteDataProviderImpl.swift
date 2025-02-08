//
//  FirebaseRemoteDataProviderImpl.swift
//  iosApp
//
//  Created by Roberto García Romero on 10/2/24.
//  Copyright © 2024 orgName. All rights reserved.
//

import shared
import FirebaseRemoteConfig

class FirebaseRemoteDataProviderImpl: FirebaseRemoteDataProvider {

    static let shared = FirebaseRemoteDataProviderImpl()

    let remoteConfig = RemoteConfig.remoteConfig()
    
    private init() {
        let settings = RemoteConfigSettings()
        settings.minimumFetchInterval = 0
        remoteConfig.configSettings = settings
        remoteConfig.setDefaults(fromPlist: "remote_config_defaults")
        remoteConfig.addOnConfigUpdateListener { configUpdate, error in
          guard let configUpdate, error == nil else {
              print("Error listening for config updates: \(String(describing: error))")
              return
          }

          print("Updated keys: \(configUpdate.updatedKeys)")

          self.remoteConfig.activate { changed, error in
              guard error == nil else {
                  print("Error")/*return self.displayError(error)*/
                  return
              }
          }
        }
    }
    
    func getListTapas(configuration: Int32, completionHandler: @escaping (ResultKMM<NSArray>?, Error?) -> Void) {
        remoteConfig.fetch { (status, error) -> Void in
          if status == .success {
              self.remoteConfig.activate()
              let remoteText = self.remoteConfig.configValue(forKey: "Tapas").stringValue ?? ""
              let decoder = JSONDecoder()
              let data = Data(remoteText.utf8)
              do {
                  let decoded = try decoder.decode([TapaITemSwift].self, from: data)
                  completionHandler(ResultKMMSuccess(data: decoded.map{$0.toBo()} as NSArray), nil)
              } catch {
                  print("Failed to decode JSON")
              }
              //completionHandler(ResultKMM<NSArray>?, nil)
          } else {
            print("Error: \(error?.localizedDescription ?? "No error available.")")
          }
        }
    }
    
    func getTapaDetail(configuration: Int32, id: String, completionHandler: @escaping (ResultKMM<TapaItemBo>?, Error?) -> Void) {
        remoteConfig.fetch { (status, error) -> Void in
          if status == .success {
              self.remoteConfig.activate()
              let remoteText = self.remoteConfig.configValue(forKey: "Tapas").stringValue ?? ""
              let decoder = JSONDecoder()
              let data = Data(remoteText.utf8)
              do {
                  let decoded = try decoder.decode([TapaITemSwift].self, from: data)
                  if let tapa = decoded.first{ $0.id == id } {
                      completionHandler(ResultKMMSuccess(data: tapa.toBo()), nil)
                  }
                  //completionHandler(ResultKMMSuccess(data: tapa.toBo()} as NSArray), nil)
              } catch {
                  print("Failed to decode JSON")
              }
              //completionHandler(ResultKMM<NSArray>?, nil)
          } else {
            print("Error: \(error?.localizedDescription ?? "No error available.")")
          }
        }
    }
}
