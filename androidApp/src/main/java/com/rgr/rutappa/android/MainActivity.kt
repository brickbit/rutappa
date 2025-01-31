package com.rgr.rutappa.android

import android.Manifest
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rgr.rutappa.android.provider.ActivityProvider
import com.rgr.rutappa.android.provider.ActivityResultLauncherProvider
import com.rgr.rutappa.android.scaffold.RutappaScaffold
import org.koin.android.ext.android.get

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        installSplashScreen()
        setContent {
            MyApplicationTheme {
                val activityProvider: ActivityProvider = get()

                val navController: NavHostController = rememberNavController()
                activityProvider.setActivity(this)

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    RutappaScaffold(navController = navController)
                }
            }
        }
    }

    override fun onStart() {
        super.onStart()
        val activityResultProvider: ActivityResultLauncherProvider = get()

        val permissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestMultiplePermissions()
        ) { permissions ->
            if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true) {
            } else {
                Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
            }
        }
        activityResultProvider.setActivityResultLauncher(permissionLauncher)
    }
}


