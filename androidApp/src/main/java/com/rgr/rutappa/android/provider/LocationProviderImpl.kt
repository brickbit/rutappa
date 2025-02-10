package com.rgr.rutappa.android.provider

import android.Manifest
import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationManager
import android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS
import androidx.core.content.ContextCompat
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY
import com.google.android.gms.tasks.CancellationTokenSource
import com.google.android.gms.tasks.Task
import com.rgr.rutappa.domain.model.ResultKMM
import com.rgr.rutappa.domain.provider.LocationProvider
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

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
    override suspend fun getLocation(): ResultKMM<Pair<String, String>> {
        return suspendCancellableCoroutine { continuation ->
            val activity = activityProvider.getActivity()
            val fusedLocationClient = activity?.let { LocationServices.getFusedLocationProviderClient(it) }
            val cancellationTokenSource = CancellationTokenSource()
            val currentTask: Task<Location>? = fusedLocationClient?.getCurrentLocation(
                PRIORITY_HIGH_ACCURACY,
                cancellationTokenSource.token
            )
            currentTask?.addOnCompleteListener { task ->
                if (task.isSuccessful && task.result != null) {
                    val coordinates = task.result
                    continuation.resume(ResultKMM.Success(Pair(coordinates.latitude.toString(),coordinates.longitude.toString())))
                } else {
                    continuation.resume(ResultKMM.Failure(Error()))
                }
            }
        }
    }

    override fun isLocationActive(): Boolean {
        val activity = activityProvider.getActivity()
        activity?.let {
            val locationManager =
                activity.getSystemService(Context.LOCATION_SERVICE) as LocationManager
            return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER) ||
                    locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)
        } ?: return false
    }

    override fun activeLocation() {
        activityProvider.getActivity()?.startActivity(Intent(ACTION_LOCATION_SOURCE_SETTINGS))
    }

    override fun areCoordinatesWithinDistance(
        lat1: Double, lon1: Double,
        lat2: Double, lon2: Double,
        maxDistance: Float
    ): Boolean {
        val loc1 = Location("").apply {
            latitude = lat1
            longitude = lon1
        }

        val loc2 = Location("").apply {
            latitude = lat2
            longitude = lon2
        }

        val distance = loc1.distanceTo(loc2) // Distance in meters
        return distance < maxDistance
    }
}