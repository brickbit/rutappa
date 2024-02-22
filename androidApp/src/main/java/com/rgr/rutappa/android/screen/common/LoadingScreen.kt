package com.rgr.rutappa.android.screen.common

import androidx.compose.foundation.background
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import com.rgr.rutappa.android.backgroundColor

@Composable
fun LoadingScreen() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(colorStops = backgroundColor(isSystemInDarkTheme()))),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator(trackColor = MaterialTheme.colorScheme.secondary)
    }
}

@Preview(device = Devices.PIXEL_3A)
@Composable
fun LoadingScreenPreview() {
    LoadingScreen()
}
