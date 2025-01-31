package com.rgr.rutappa.android.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.LocationProvider

class LocationProviderImpl(
    private val activityProvider: ActivityProvider,
    private val permissionLauncherProvider: ActivityResultLauncherProvider
) : LocationProvider {

    private val permissions = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.ACCESS_COARSE_LOCATION
    )

    override fun requestPermission() {
        val permissionLauncher = permissionLauncherProvider.getActivityResultLauncher()
        permissionLauncher?.launch(permissions)
    }
    override fun hasPermission(): Boolean {
        val activity = activityProvider.getActivity()
        return permissions.all {
            activity?.let { it1 -> ContextCompat.checkSelfPermission(it1, it) } == PackageManager.PERMISSION_GRANTED
        }
    }
    @SuppressLint("MissingPermission")
    override fun getLocation(): ResultKMM<Pair<String, String>> {
        val activity = activityProvider.getActivity()
        val fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
        val lastLocation = fusedLocationClient?.lastLocation
        if(lastLocation?.isSuccessful == true) {
            val coordinates = lastLocation.result
            return ResultKMM.Success(Pair(coordinates.latitude.toString(),coordinates.longitude.toString()))
        } else {
            return ResultKMM.Failure(Error())
        }
    }
}