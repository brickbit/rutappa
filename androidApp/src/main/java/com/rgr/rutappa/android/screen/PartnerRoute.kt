package com.rgr.rutappa.android.screen

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.core.graphics.toColor
import androidx.core.graphics.toColorInt
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.screen.common.Header
import com.rgr.rutappa.android.screen.common.Menu
import com.rgr.rutappa.android.screen.common.SocialWall
import com.rgr.rutappa.app.viewModel.PartnerViewModel
import com.rgr.rutappa.domain.model.PartnersListBO
import kotlinx.coroutines.Dispatchers
import org.koin.androidx.compose.koinViewModel

@Composable
fun PartnerRoute(
    navigateToTapa: () -> Unit,
    navigateToLogout: () -> Unit,
    viewModel: PartnerViewModel = koinViewModel()
) {
    val state = viewModel.state.collectAsState().value
    if(state.logout || state.error) {
        navigateToLogout()
    }
    LaunchedEffect(Unit) {
        viewModel.getPartners(R.xml.remote_config_defaults)
    }

    PartnersView(
        partners = state.partners,
        logout = state.logout,
        navigateToLogout = { viewModel.logout() },
        deleteAccount = { viewModel.deleteAccount() },
        navigateToTapa = navigateToTapa
    )
}

@Composable
fun PartnersView(
    partners: PartnersListBO?,
    logout: Boolean,
    navigateToLogout: () -> Unit,
    deleteAccount: () -> Unit,
    navigateToTapa: () -> Unit
) {
    val showMenu = remember { mutableStateOf(false) }
    val openLogoutDialog = remember { mutableStateOf(false) }

    LaunchedEffect(logout) {
        if(logout) navigateToLogout()
    }
    Box(
        modifier = Modifier
            .fillMaxSize(),
        contentAlignment = Alignment.BottomCenter
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .background(
                    Brush.verticalGradient(
                        colorStops = arrayOf(
                            0.05f to Color(0xFFFFFFFF),
                            0.5f to Color(0xFFf3e7cd)
                        )
                    )
                )
        ) {
            PartnerGreetings( onShowMenu = { showMenu.value = true } )
            partners?.let {
                PartnerListView(it)
            }
        }
        SocialWall()
    }
    if(showMenu.value) {
        Menu(
            onTapaClicked = { navigateToTapa() },
            onPartnersClicked = { showMenu.value = false },
            onCloseClicked = { showMenu.value = false },
            onLogoutClicked = {
                showMenu.value = false
                openLogoutDialog.value = true
            }
        )
    }
    if(openLogoutDialog.value) {
        LogoutDialog(
            onDismissDialog = { openLogoutDialog.value = false },
            deleteAccount = deleteAccount,
            navigateToLogout = navigateToLogout,
            onOpenLogoutDialog = { openLogoutDialog.value = false }
        )
    }
}

@Composable
fun LogoutDialog(
    onDismissDialog: () -> Unit,
    deleteAccount: () -> Unit,
    navigateToLogout: () -> Unit,
    onOpenLogoutDialog: () -> Unit
) {
    Dialog(
        onDismissRequest = onDismissDialog
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
                onClick = { navigateToLogout() }) {
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
                onClick = { onOpenLogoutDialog() }) {
                Text(
                    text = stringResource(R.string.main_stay_logged),
                    style = MaterialTheme.typography.bodyLarge
                )
            }
        }
    }
}

@Composable
fun PartnerListView(
    partners: PartnersListBO
) {
    val context = LocalContext.current

    partners.categories.map { categories ->
        Column(modifier = Modifier.background(
            if(categories.background == "000000") {
                Color.Transparent
            } else {
                Color(android.graphics.Color.parseColor("#${categories.background}"))
            })) {
            Text(
                modifier = Modifier.fillMaxWidth().padding(vertical = 20.dp),
                text = categories.name,
                textAlign = TextAlign.Center
            )
            categories.partners.map { partner ->
                val listener = object : ImageRequest.Listener {}
                val imageRequest = ImageRequest.Builder(context)
                    .data(partner.image)
                    .listener(listener)
                    .dispatcher(Dispatchers.IO)
                    .memoryCacheKey(partner.image)
                    .diskCacheKey(partner.image)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .build()
                AsyncImage(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 36.dp, vertical = 8.dp)
                        .clickable {
                            val browserIntent = Intent(Intent.ACTION_VIEW, Uri.parse(partner.link))
                            context.startActivity(browserIntent)
                        }
                        .padding(bottom = if(categories == partners.categories[partners.categories.size -1]) 100.dp else if(categories == partners.categories[0]) 24.dp else 0.dp),
                    model = imageRequest,
                    contentDescription = null,
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}

@Composable
fun PartnerGreetings(
    onShowMenu: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Header(
            hasLogout = true,
            onShowMenu = onShowMenu
        )
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.gretings_1),
                style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.secondary),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
            Text(
                modifier = Modifier.padding(top = 8.dp),
                text = stringResource(R.string.gretings_2),
                style = MaterialTheme.typography.titleMedium.copy(MaterialTheme.colorScheme.secondary),
                fontSize = 12.sp,
                textAlign = TextAlign.Center
            )
        }
    }
}

@Preview
@Composable
fun PartnersPreview() {
    MyApplicationTheme {
        PartnersView(
            logout = false,
            navigateToLogout = {},
            deleteAccount = {},
            navigateToTapa = {},
            partners = null
        )
    }
}