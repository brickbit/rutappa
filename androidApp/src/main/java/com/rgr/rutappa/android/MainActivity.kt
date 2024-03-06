package com.rgr.rutappa.android

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.NavHostController
import androidx.navigation.compose.rememberNavController
import com.rgr.rutappa.android.provider.ActivityProvider
import com.rgr.rutappa.android.scaffold.RutappaScaffold
import com.rgr.rutappa.android.screen.ResultScreen
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
                    ResultScreen()
                    //RutappaScaffold(navController = navController)
                }
            }
        }
    }
}


