package com.rgr.rutappa.android.scaffold

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import com.rgr.rutappa.android.navigation.Navigator

@Composable
fun RutappaScaffold(
    navController: NavHostController,
) {
    Scaffold(
        topBar = { }
    ) { paddingValues ->
        Box(
            modifier = Modifier.padding(paddingValues)
        ) {
            Navigator(navController = navController)
        }
    }
}