package com.rgr.rutappa.android.screen

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS
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
import androidx.compose.runtime.MutableState
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
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.core.content.ContextCompat
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import coil.compose.AsyncImage
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.accompanist.permissions.rememberPermissionState
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.backgroundColor
import com.rgr.rutappa.android.screen.common.AlertDialogError
import com.rgr.rutappa.android.screen.common.Header
import com.rgr.rutappa.android.screen.common.LoadingScreen
import com.rgr.rutappa.android.screen.common.Menu
import com.rgr.rutappa.android.screen.common.SocialWall
import com.rgr.rutappa.app.state.VoteStatus
import com.rgr.rutappa.app.viewModel.DetailViewModel
import com.rgr.rutappa.domain.error.FirestoreError
import com.rgr.rutappa.domain.model.LocalItemBo
import com.rgr.rutappa.domain.model.TapaItemBo
import org.koin.androidx.compose.koinViewModel
import kotlin.math.roundToInt

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun DetailRoute(
    viewModel: DetailViewModel = koinViewModel(),
    tapaId: String,
    navigateToTapas: () -> Unit,
    navigateToPartners: () -> Unit,
    navigateToLogout: () -> Unit
) {
    val state = viewModel.state.collectAsState().value
    val errorState = viewModel.errorState.collectAsState().value
    val openAlertDialog = remember { mutableStateOf(false) }
    val context = LocalContext.current
    val locationPermissionState: MutableState<Boolean?> = remember { mutableStateOf(null) }
    val permissions = listOf(
        Manifest.permission.CAMERA,
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    val multiPermissionState = rememberMultiplePermissionsState(permissions)
    val lifecycleOwner = LocalLifecycleOwner.current


    LaunchedEffect(Unit) {
        lifecycleOwner.lifecycle.addObserver(object : DefaultLifecycleObserver {
            override fun onResume(owner: LifecycleOwner) {
                viewModel.checkPermission()
                viewModel.checkGPSEnabled()
            }
        })
        multiPermissionState.launchMultiplePermissionRequest()
        viewModel.getDetail(
            configuration = R.xml.remote_config_defaults,
            id = tapaId
        )
    }
    if(state.logout || state.error) {
        navigateToLogout()
    }
    if(state.isLoading) {
        LoadingScreen()
    } else {
        state.tapa?.let {
            DetailScreen(
                tapa = it,
                hasLocationPermission = state.hasLocationPermission,
                isGPSActive = state.isGPSActive,
                isInRadius = state.isInRadius,
                voteStatus = state.voteStatus,
                getLocation = { viewModel.manageLocation(updateLocationStatus = {
                    locationPermissionState.value = it
                }, onDeniedPermission = {
                    val intent = Intent(ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                        data = Uri.fromParts("package", context.packageName, null)
                    }
                    context.startActivity(intent)
                } )},
                onVoteClicked = { vote, tapa ->
                    viewModel.vote(vote = vote, tapa =  tapa)
                },
                navigateToTapas = navigateToTapas,
                navigateToPartners = navigateToPartners,
                deleteAccount = { viewModel.deleteAccount() },
                logout = { viewModel.logout() },
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
    hasLocationPermission: Boolean?,
    isGPSActive: Boolean,
    isInRadius: Boolean?,
    voteStatus: VoteStatus,
    getLocation: () -> Unit,
    onVoteClicked: (Int, String) -> Unit,
    navigateToTapas: () -> Unit,
    navigateToPartners: () -> Unit,
    deleteAccount: () -> Unit,
    logout: () -> Unit,
) {
    val openLogoutDialog = remember { mutableStateOf(false) }
    val showMenu = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
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
                            hasLogout = true,
                            onShowMenu = { showMenu.value = true }
                        )
                    }
                    Column {
                        TapaInfo(tapa)
                    }
                }
                legumesSection(tapa.legumes)
                item {
                    VoteSectionContent(
                        tapa = tapa,
                        hasLocationPermission = hasLocationPermission,
                        isGPSActive = isGPSActive,
                        isInRadius = isInRadius,
                        voteStatus = voteStatus,
                        getLocation = getLocation,
                        onVoteClicked = onVoteClicked,
                    )
                }
            }
            SocialWall()
        }
    }
    if(showMenu.value) {
        Menu(
            onTapaClicked = {
                showMenu.value = false
                navigateToTapas()
            },
            onPartnersClicked = {
                showMenu.value = false
                navigateToPartners()
            },
            onCloseClicked = { showMenu.value = false },
            onLogoutClicked = {
                showMenu.value = false
                openLogoutDialog.value = true
            }
        )
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
fun VoteSectionContent(
    tapa: TapaItemBo,
    hasLocationPermission: Boolean?,
    isGPSActive: Boolean,
    isInRadius: Boolean?,
    voteStatus: VoteStatus,
    getLocation: () -> Unit,
    onVoteClicked: (Int, String) -> Unit,
) {
    val context = LocalContext.current

    when(voteStatus) {
        VoteStatus.UNKNOWN -> {
            RequestLocationButton(
                text = getLocationButtonText(hasLocationPermission = hasLocationPermission, isGPSActive = isGPSActive, isInRadius = isInRadius, context = context),
                onClickAction = {
                    getLocation()
                }
            )
        }
        VoteStatus.CAN_VOTE -> VoteSection(
            voted = false,
            onVoteClicked = { onVoteClicked(it, tapa.id) }
        )
        VoteStatus.VOTED -> VoteSection(
            voted = true,
            onVoteClicked = { onVoteClicked(it, tapa.id) }
        )
    }
}

fun getLocationButtonText(
    hasLocationPermission: Boolean?,
    isGPSActive: Boolean,
    isInRadius: Boolean?,
    context: Context
): String {
    when(hasLocationPermission) {
        true -> {
            if(!isGPSActive) {
                return "Pulse aquí para activar el GPS"
            }
            return when(isInRadius) {
                true -> "Pulse aquí para votar"
                false -> context.getString(R.string.can_not_vote)
                null -> "Pulse aquí para obtener la ubicación"
            }
        }
        false -> return "Permiso denegado vaya a ajustes para activar el permiso"
        null -> return "Pulse aquí para solicitar permiso de ubicación"
    }
}

@Composable
fun RequestLocationButton(
    text: String,
    onClickAction: () -> Unit
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 136.dp, top = 16.dp, start = 16.dp, end = 16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.secondary
        ),
        onClick = { onClickAction() }
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
    }
}

@Composable
fun ErrorRequestLocationContent(
    text: String,
    buttonText: String,
    onClickAction: () -> Unit
) {
    Column {
        Text(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 8.dp),
            text = text,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.Center
        )
        Button(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 136.dp, start = 16.dp, end = 16.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.secondary
            ),
            onClick = { onClickAction() }
        ) {
            Text(
                text = buttonText,
                style = MaterialTheme.typography.bodyLarge
            )
        }
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
        modifier = Modifier
            .padding(horizontal = 20.dp)
            .padding(top = 16.dp, bottom = 100.dp)
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
                    facebook = "",
                    longitude = "",
                    latitude = ""
                )
            ),
            hasLocationPermission = false,
            isGPSActive = false,
            isInRadius = false,
            voteStatus = VoteStatus.VOTED,
            getLocation = {},
            onVoteClicked={_,_ ->},
            deleteAccount = {},
            logout = {},
            navigateToPartners = {},
            navigateToTapas = {},
        )
    }
}
 