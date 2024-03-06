package com.rgr.rutappa.android.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.rgr.rutappa.android.screen.common.LoadingScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ResultScreen(
    viewModel: ResultViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value

    when(state) {
        ResultState.Idle -> {
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Button(onClick = { viewModel.calculateResult() }) {
                    Text(text = "Obtener resultado", color = Color.Black)
                }
            }
        }
        is ResultState.Loaded -> {
            LazyColumn{
                items(state.votes) {
                    Row {
                        Text(text = "Id Tapa: ${it.first}")
                        Text(text = "VOTOS: ${it.second}")
                    }
                }
            }
        }
        ResultState.Loading -> { LoadingScreen()}
    }

}