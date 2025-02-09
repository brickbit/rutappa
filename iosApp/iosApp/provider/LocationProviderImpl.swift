//
//  LocationProviderImpl.swift
//  iosApp
//
//  Created by Roberto on 1/2/25.
//  Copyright © 2025 orgName. All rights reserved.
//

import Foundation
import shared
import CoreLocation

class LocationProviderImpl: NSObject, LocationProvider, ObservableObject, CLLocationManagerDelegate {
    private var locationManager: CLLocationManager?
    
    @Published var userLocation: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus?
    
    override init() {
        super.init()
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        locationManager?.requestAlwaysAuthorization()
    }
    
    func getLocation() -> ResultKMM<KotlinPair<NSString, NSString>> {
        if authorizationStatus == .authorizedAlways || authorizationStatus == .authorizedWhenInUse {
            if CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self) {
                if CLLocationManager.isRangingAvailable() {
                    locationManager?.requestLocation()
                    return ResultKMMSuccess(data: KotlinPair(first: "\(userLocation?.coordinate.latitude ?? 0.0)" as NSString, second: "\(userLocation?.coordinate.longitude ?? 0.0)" as NSString))
                }
            }
        }
        return ResultKMMSuccess(data: KotlinPair(first: "0" as NSString, second: "0" as NSString))  
    }
    
    func hasPermission() -> Bool {
        let status = locationManager?.authorizationStatus
        switch status {
        case .notDetermined:
            return false
        case .restricted, .denied:
            return false
        case .authorizedWhenInUse, .authorizedAlways:
            return true
        @unknown default:
           return false
        }
    }
    
    func requestPermission() {
        locationManager?.requestWhenInUseAuthorization()
    }
    
    // Delegate method - called when location updates
    func locationManager(_ manager: CLLocationManager, didUpdateLocations locations: [CLLocation]) {
        guard let location = locations.last else { return }
        DispatchQueue.main.async {
            self.userLocation = location
        }
    }

    // Delegate method - called when authorization status changes
    func locationManagerDidChangeAuthorization(_ manager: CLLocationManager) {
        DispatchQueue.main.async {
            self.authorizationStatus = manager.authorizationStatus
        }
    }
    
    func locationManager(_ manager: CLLocationManager, didChangeAuthorization status: CLAuthorizationStatus) {
        if status == .authorizedAlways {
            if CLLocationManager.isMonitoringAvailable(for: CLBeaconRegion.self) {
                if CLLocationManager.isRangingAvailable() {
                    // do stuff
                }
            }
        }
    }
    
}
