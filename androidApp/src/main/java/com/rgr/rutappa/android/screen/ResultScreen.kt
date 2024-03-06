package com.rgr.rutappa.android.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.core.content.FileProvider
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.screen.common.LoadingScreen
import org.koin.androidx.compose.koinViewModel

@Composable
fun ResultScreen(
    viewModel: ResultViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    val context = LocalContext.current

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
            MyApplicationTheme {
                LazyColumn{
                    items(state.votes) {
                        Column {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(text = "Id Tapa: ${it.first}")
                                Text(text = "VOTOS: ${it.second}")
                            }
                            Divider()
                        }
                    }

                }
            }
        }
        ResultState.Loading -> { LoadingScreen()}
    }

}