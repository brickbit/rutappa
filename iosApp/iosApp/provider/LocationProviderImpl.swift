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
import UIKit

class LocationProviderImpl: NSObject, LocationProvider, ObservableObject, CLLocationManagerDelegate {
    
    func hasPermission() async throws -> KotlinBoolean? {
        let status = locationManager?.authorizationStatus
        switch status {
        case .notDetermined:
            return false
        case .restricted, .denied:
            return false
        case .authorizedWhenInUse, .authorizedAlways:
            return true
        case .none:
            return false
        @unknown default:
           return false
        }
    }
    
    func getLocation() async throws -> ResultKMM<TapaLocation> {
        locationManager?.requestLocation()
        let latitude = userLocation?.coordinate.latitude ?? 0.0
        let longitude = userLocation?.coordinate.longitude ?? 0.0
        
        return ResultKMMSuccess(data: TapaLocation(latitude: "\(latitude)", longitude: "\(longitude)"))
    }
    
    func getLocationIOS() async throws ->TapaLocation{
        locationManager?.requestLocation()
        let latitude = userLocation?.coordinate.latitude ?? 0.0
        let longitude = userLocation?.coordinate.longitude ?? 0.0
        
        return TapaLocation(latitude: "\(latitude)", longitude: "\(longitude)")
    }
    
    private let localRepository: LocalRepositoryImpl = LocalRepositoryImpl.shared
    private var locationManager: CLLocationManager?
    
    @Published var userLocation: CLLocation?
    @Published var authorizationStatus: CLAuthorizationStatus?
    
    override init() {
        super.init()
        locationManager = CLLocationManager()
        locationManager?.delegate = self
        locationManager?.requestAlwaysAuthorization()
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
        case .none:
            return false
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
    
    func locationManager(_ manager: CLLocationManager, didFailWithError error: Error) {
        print("Location request failed with error: \(error.localizedDescription)")
    }
    
    func isLocationActive() -> Bool {
        return CLLocationManager.locationServicesEnabled()
    }
    
    func activeLocation() {
        guard let settingsURL = URL(string: UIApplication.openSettingsURLString) else { return }
        
        if UIApplication.shared.canOpenURL(settingsURL) {
            UIApplication.shared.open(settingsURL)
        }
    }
    
    func areCoordinatesWithinDistance(lat1: Double, lon1: Double, lat2: Double, lon2: Double, maxDistance: Float) -> Bool {
        let location1 = CLLocation(latitude: lat1, longitude: lon1)
        let location2 = CLLocation(latitude: lat2, longitude: lon2)

        let distance = Float(location1.distance(from: location2)) // Distance in meters
        print("lat1: \(lat1) lon1: \(lon1) lat2: \(lat2) lon2: \(lon2) distance: \(distance)")
        return distance < maxDistance
    }
    
}
