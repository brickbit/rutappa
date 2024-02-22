package com.rgr.rutappa.android.screen

import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
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
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.rgr.rutappa.android.MainActivity
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.backgroundColor
import com.rgr.rutappa.android.screen.common.Header
import com.rgr.rutappa.android.screen.common.LoadingScreen
import com.rgr.rutappa.android.screen.common.SocialWall
import com.rgr.rutappa.app.state.MainState
import com.rgr.rutappa.app.viewModel.MainViewModel
import com.rgr.rutappa.domain.error.RemoteConfigError
import com.rgr.rutappa.domain.model.LocalItemBo
import com.rgr.rutappa.domain.model.TapaItemBo
import org.koin.androidx.compose.koinViewModel

@Composable
fun MainRoute(
    viewModel: MainViewModel = koinViewModel(),
    navigateToDetail: (String) -> Unit,
    navigateToLogin: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val errorState = viewModel.errorState.collectAsState().value
    val context = LocalContext.current

    LaunchedEffect(key1 = Unit) {
        viewModel.getListTapas(R.xml.remote_config_defaults)
    }

    BackHandler {
        (context as MainActivity).finish()
    }

    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        when(errorState) {
            RemoteConfigError.ItemNotFound -> {}
            RemoteConfigError.NoActivityError -> {}
            RemoteConfigError.NoError -> {}
            RemoteConfigError.RemoteConfigTaskFailed -> {}
        }

        when(state) {
            is MainState.Loaded -> MainScreen(
                tapas = state.tapas,
                navigateToDetail = navigateToDetail,
                deleteAccount = { viewModel.deleteAccount() },
                logout = { viewModel.logout() }
            )
            MainState.Loading -> LoadingScreen()
            MainState.Logout -> {
                navigateToLogin()
            }
            is MainState.MainStateError -> {
                Toast.makeText(context, "Error al eliminar cuenta. Inicie sesión y elimine la cuenta para continuar", Toast.LENGTH_LONG).show()
                navigateToLogin()
            }
        }
    }
}

@Composable
fun MainScreen(
    tapas: List<TapaItemBo>,
    navigateToDetail: (String) -> Unit,
    deleteAccount: () -> Unit,
    logout: () -> Unit
) {
    val openLogoutDialog = remember { mutableStateOf(false) }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(colorStops = backgroundColor(isSystemInDarkTheme()))
            ),
        contentAlignment = Alignment.BottomCenter
    ) {
        LazyColumn(
            modifier = Modifier.fillMaxHeight()
        ) {
            item {
                Column {
                    Header {
                        openLogoutDialog.value = true
                    }
                    Text(
                        modifier = Modifier
                            .padding(horizontal = 16.dp)
                            .padding(top = 8.dp, bottom = 16.dp),
                        text = stringResource(R.string.main_subtitle),
                        style = MaterialTheme.typography.displayMedium
                    )
                }
            }
            items(tapas) { tapa ->
                Column(
                    modifier = Modifier.fillMaxHeight(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TapaItemComposable(
                        tapa = tapa,
                        onItemClicked = { navigateToDetail(it) }
                    )
                    Divider(
                        thickness = 1.dp,
                        color = MaterialTheme.colorScheme.secondary.copy(alpha = 0.3f),
                        modifier = Modifier
                            .fillMaxWidth(0.9f)
                            .padding(vertical = 8.dp)
                    )
                }
            }
        }
        SocialWall()
    }
    if(openLogoutDialog.value) {
        Dialog(
            onDismissRequest = { openLogoutDialog.value = false }
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(300.dp)
                    .padding(16.dp)
                    .background(
                        color = MaterialTheme.colorScheme.primary,
                        shape = RoundedCornerShape(16.dp)
                    ),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    modifier = Modifier.padding(bottom = 36.dp, top = 16.dp),
                    text = stringResource(R.string.main_logout_text_dialog),
                    textAlign = TextAlign.Center,
                )
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Red
                    ),
                    onClick = { deleteAccount() }) {
                    Text(
                        text = stringResource(R.string.main_delete_account),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = { logout() }) {
                    Text(
                        text = stringResource(R.string.main_logout),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
                Button(
                    modifier = Modifier
                        .fillMaxWidth(0.9f)
                        .padding(bottom = 16.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondary
                    ),
                    onClick = { openLogoutDialog.value = false }) {
                    Text(
                        text = stringResource(R.string.main_stay_logged),
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
        }
    }
}

@Composable
fun TapaItemComposable(
    tapa: TapaItemBo,
    onItemClicked: (String) -> Unit
) {
    Row(
        modifier = Modifier
            .padding(vertical = 8.dp, horizontal = 20.dp)
            .clickable { onItemClicked(tapa.id) }
    ) {
        AsyncImage(
            modifier = Modifier
                .size(120.dp)
                .clip(RoundedCornerShape(16.dp)),
            model = tapa.photo,
            contentDescription = null,
            contentScale = ContentScale.Crop
        )
        Column(
            modifier = Modifier.padding(start = 12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                Text(
                    text = tapa.name,
                    maxLines = 3,
                    overflow = TextOverflow.Ellipsis,
                    style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.secondary)
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Image(
                        modifier = Modifier
                            .size(24.dp)
                            .padding(end = 8.dp),
                        painter = painterResource(id = R.drawable.ic_location),
                        contentDescription = null,
                        colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.secondary)
                    )
                    Text(
                        text = tapa.local.name,
                        style = MaterialTheme.typography.displaySmall
                    )
                }
                Text(
                    text = tapa.shortDescription,
                    style = MaterialTheme.typography.displaySmall.copy(
                        MaterialTheme.colorScheme.secondary.copy(alpha = 0.6f)
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

@Preview(device = Devices.PIXEL_3A)
@Composable
fun MainScreenPreview() {
    MyApplicationTheme {
        MainScreen(
            tapas = listOf(
                TapaItemBo(
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
            ),
            navigateToDetail = {},
            deleteAccount = {},
            logout = {}
        )
    }
}