package com.rgr.rutappa.android.screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.ShadowColor
import com.rgr.rutappa.app.navigation.Routes
import com.rgr.rutappa.app.state.SplashState
import com.rgr.rutappa.app.viewModel.SplashViewModel
import org.koin.androidx.compose.koinViewModel

@Composable
fun SplashRoute(
    viewModel: SplashViewModel = koinViewModel(),
    navigateTo: (Routes) -> Unit
) {
    val state = viewModel.state.collectAsState().value
    when(state) {
        is SplashState.Finished -> SplashScreen(
            navigateTo = { navigateTo(state.route) }
        )
        SplashState.Init -> SplashScreen()
    }
}

@Composable
fun SplashScreen(
    navigateTo: () -> Unit = {}
) {
    LaunchedEffect(key1 = Unit) {
        navigateTo()
    }
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Image(
                modifier = Modifier.fillMaxSize(),
                painter = painterResource(id = R.drawable.beans_background),
                contentDescription = null,
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Brush.verticalGradient(colorStops = ShadowColor))
            )
            Image(
                painter = painterResource(id = R.drawable.rutappa_splash),
                contentDescription = null
            )
        }
        Column(
            modifier = Modifier.padding(bottom = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(8.dp, Alignment.CenterVertically)
        ) {
            Image(
                modifier = Modifier
                    .width(200.dp),
                painter = painterResource(id = R.drawable.legumbres_tapas_white),
                contentDescription = null
            )
            Text(
                text = stringResource(R.string.copyrigth),
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = MaterialTheme.colorScheme.primary
                )
            )
            Image(
                modifier = Modifier
                    .size(90.dp)
                    .padding(bottom = 8.dp),
                painter = painterResource(id = R.drawable.tierra_sabor_logo),
                contentDescription = null
            )
        }
    }
}

@Preview(device = Devices.PIXEL_3A)
@Composable
fun SplashScreenPreview() {
    MyApplicationTheme {
        SplashScreen()
    }
}