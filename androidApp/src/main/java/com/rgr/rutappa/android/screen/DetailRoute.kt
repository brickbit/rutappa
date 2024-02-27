package com.rgr.rutappa.android.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListScope
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import coil.compose.AsyncImage
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.backgroundColor
import com.rgr.rutappa.android.screen.common.AlertDialogError
import com.rgr.rutappa.android.screen.common.Header
import com.rgr.rutappa.android.screen.common.LoadingScreen
import com.rgr.rutappa.android.screen.common.SocialWall
import com.rgr.rutappa.app.state.DetailState
import com.rgr.rutappa.app.viewModel.DetailViewModel
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.model.LocalItemBo
import com.rgr.rutappa.domain.model.TapaItemBo
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@Composable
fun DetailRoute(
    viewModel: DetailViewModel = koinViewModel(),
    tapaId: String,
) {
    val state = viewModel.state.collectAsState().value
    val errorState = viewModel.errorState.collectAsState().value
    val openAlertDialog = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        viewModel.getDetail(
            configuration = R.xml.remote_config_defaults,
            id = tapaId
        )
    }
    when (state) {
        is DetailState.Loaded -> {
            DetailScreen(
                tapa = state.tapa,
                voted = state.voted,
                onVoteClicked = { vote, tapa ->
                    viewModel.vote(vote = vote, tapa =  tapa)
                }
            )
        }
        DetailState.Loading -> LoadingScreen()
        is DetailState.Voted -> {
            DetailScreen(
                tapa = state.tapa,
                voted = true,
                onVoteClicked = { vote, tapa ->
                    viewModel.vote(vote = vote, tapa =  tapa)
                }
            )
        }
    }
    when (errorState) {
        FirestoreError.NoError -> {}
        FirestoreError.NoUserError, FirestoreError.ReadingError, FirestoreError.WritingError, FirestoreError.TapaVotedYet -> {
            AlertDialogError(
                description = {
                    when(errorState) {
                        FirestoreError.NoError -> {}
                        FirestoreError.NoUserError -> Text(text = stringResource(R.string.error_detail_firestore_other), color = MaterialTheme.colorScheme.secondary)
                        FirestoreError.ReadingError -> Text(text = stringResource(R.string.error_detail_firestore_other), color = MaterialTheme.colorScheme.secondary)
                        FirestoreError.TapaVotedYet -> Text(text = stringResource(R.string.error_detail_firestore_voted_yet), color = MaterialTheme.colorScheme.secondary)
                        FirestoreError.WritingError -> Text(text = stringResource(R.string.error_detail_firestore_other), color = MaterialTheme.colorScheme.secondary)
                    }
                },
                onDismissRequest = {
                    viewModel.clearError()
                    openAlertDialog.value = false
                },
            )
        }
    }
}

@Composable
fun DetailScreen(
    tapa: TapaItemBo,
    voted: Boolean,
    onVoteClicked: (Int, String) -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize().background(Color.White)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = backgroundColor(isSystemInDarkTheme()))),
            contentAlignment = Alignment.BottomCenter

        ) {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
            ) {
                item {
                    TapaCover(tapa = tapa)
                    Column {
                        TapaInfo(tapa)
                    }
                }
                legumesSection(tapa.legumes)
                item {
                    Column {
                        VoteSection(
                            voted = voted,
                            onVoteClicked = { onVoteClicked(it, tapa.id) }
                        )
                    }
                }
            }
            SocialWall()
        }
    }
}

@Composable
fun TapaCover(
    tapa: TapaItemBo
) {
    Box(
        contentAlignment = Alignment.TopStart
    ) {
        AsyncImage(
            modifier = Modifier
                .padding(top = 90.dp)
                .height(350.dp),
            model = tapa.photo,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Header(
            hasLogout = false
        )
    }
}

@Composable
fun TapaInfo(
    tapa: TapaItemBo
) {
    val context = LocalContext.current
    Column(
        modifier = Modifier
            .padding(horizontal = 20.dp, vertical = 16.dp)
    ) {
        Column(
            modifier = Modifier.padding(top = 16.dp)
        ) {
            Text(
                text = tapa.name.uppercase(),
                style = MaterialTheme.typography.bodyLarge
            )
            Text(
                text = stringResource(
                    R.string.detail_location,
                    tapa.local.name,
                    tapa.local.province
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Row {
                if(tapa.local.instagram.isNotEmpty()) {
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setData(Uri.parse(tapa.local.instagram))
                                ContextCompat.startActivity(context, intent, null)
                            },
                        painter = painterResource(id = R.drawable.ic_instagram),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                    )
                }
                if(tapa.local.facebook.isNotEmpty()) {
                    Image(
                        modifier = Modifier
                            .padding(8.dp)
                            .size(24.dp)
                            .clickable {
                                val intent = Intent(Intent.ACTION_VIEW)
                                intent.setData(Uri.parse(tapa.local.facebook))
                                ContextCompat.startActivity(context, intent, null)
                            },
                        painter = painterResource(id = R.drawable.ic_facebook),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                    )
                }
            }
        }
        Text(
            text = tapa.shortDescription,
            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary)
        )
    }
}

fun LazyListScope.legumesSection(
    legumes: List<String>
) {
    items(legumes) { legume ->
        Row(
            modifier = Modifier.padding(horizontal = 20.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Image(
                modifier = Modifier.size(36.dp),
                painter = painterResource(id = getLegumeImage(legume)),
                contentDescription = null,
                colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
            )
            Text(
                text = legume,
                style = MaterialTheme.typography.bodySmall.copy(MaterialTheme.colorScheme.secondary),
                maxLines = 3,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center
            )
        }
    }
}

fun getLegumeImage(legume: String): Int {
    return when(legume.split(" ").getOrNull(0)) {
        "Lenteja" -> R.drawable.lentejas
        "Garbanzo" -> R.drawable.garbanzos
        else -> R.drawable.alubias
    }
}

@Composable
fun VoteSection(
    voted: Boolean,
    onVoteClicked: (Int) -> Unit
) {
    var sliderPosition by remember { mutableFloatStateOf(50f) }

    Column(
        modifier = Modifier.padding(horizontal = 20.dp).padding(top = 16.dp, bottom = 48.dp)
    ) {
        Text(
            text = stringResource(R.string.rate_detail_tapa),
            style = MaterialTheme.typography.bodyLarge
        )
        Text(
            text = stringResource(R.string.detail_rate_process),
            style = MaterialTheme.typography.bodyMedium.copy(MaterialTheme.colorScheme.secondary)
        )
        if(!voted) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.zero),
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    modifier = Modifier.weight(8f),
                    value = sliderPosition,
                    onValueChange = { sliderPosition = it },
                    colors = SliderDefaults.colors(
                        thumbColor = MaterialTheme.colorScheme.secondary,
                        activeTrackColor = MaterialTheme.colorScheme.secondary,
                        inactiveTrackColor = MaterialTheme.colorScheme.secondaryContainer,
                    ),
                    steps = 50,
                    valueRange = 0f..100f
                )
                Text(
                    modifier = Modifier.weight(1f),
                    text = stringResource(R.string.one_hundred),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 36.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.secondary
                ),
                onClick = { onVoteClicked(sliderPosition.roundToInt()) }
            ) {
                Image(
                    painter = painterResource(id = R.drawable.ic_vote),
                    contentDescription = null,
                    colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                )
                Text(
                    text = stringResource(R.string.detail_rate_result, sliderPosition.roundToInt()),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        } else {
            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
                    .padding(bottom = 32.dp),
                text = stringResource(R.string.detail_voted_yet),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview(device = Devices.PIXEL_3A)
@Composable
fun DetailScreenPreview() {
    MyApplicationTheme {
        DetailScreen(
            tapa = TapaItemBo(
                id = "1",
                name = "Nombre de tapa 1",
                photo = "https://destapalaslegumbres.es/wp-content/uploads/2023/10/LA-VINA-DE-PATXI-PATXI-IRISARRI-1-scaled.jpg",
                shortDescription = "Lorem ipsum dolor sit amet, consectetur adipiscing elit. Proin sit amet mollis metus. Sed sollicitudin placerat ante quis rutrum. Aliquam eget nibh scelerisque, auctor lacus eu, dictum nulla. Morbi posuere magna a magna varius, quis consequat ante sodales. Morbi sodales eget erat a dignissim. Phasellus elementum est pulvinar scelerisque venenatis. Duis condimentum risus nisl.",
                legumes = listOf("Lentejas", "Garbanzos"),
                local = LocalItemBo(
                    id = "1",
                    name = "Taberna los Cazurros",
                    province = "León",
                    instagram = "",
                    facebook = ""
                )
            ),
            onVoteClicked={_,_ ->},
            voted = false
        )
    }
}
 