package com.rgr.rutappa.android.screen

import android.app.Activity
import android.app.Activity.RESULT_OK
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_UPDATE_CURRENT
import android.content.Intent
import android.content.Intent.ACTION_VIEW
import android.net.Uri
import android.provider.Settings
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.IntentSenderRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Devices
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat.startActivity
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider
import com.rgr.rutappa.android.MainActivity
import com.rgr.rutappa.android.MyApplicationTheme
import com.rgr.rutappa.android.R
import com.rgr.rutappa.android.backgroundColor
import com.rgr.rutappa.android.provider.IntentProvider
import com.rgr.rutappa.android.provider.IntentSenderProvider
import com.rgr.rutappa.android.screen.common.AlertDialogError
import com.rgr.rutappa.android.screen.common.SocialWall
import com.rgr.rutappa.app.state.LoginState
import com.rgr.rutappa.app.viewModel.LoginViewModel
import com.rgr.rutappa.domain.error.LoginError
import kotlinx.coroutines.launch
import org.koin.androidx.compose.get
import org.koin.androidx.compose.koinViewModel



@Composable
fun LoginRoute(
    viewModel: LoginViewModel = koinViewModel(),
    navigateToMain: () -> Unit
) {
    val context = LocalContext.current
    val RC_SIGN_IN = 9001
    val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
        .requestIdToken("747962270451-ksshp66gkvcrrpa8b8kivjch4nmcnoh0.apps.googleusercontent.com")
        .requestEmail()
        .build()

    val mGoogleSignInClient = GoogleSignIn.getClient(context, gso)
    val launcher2 = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
                try {
                    // Google Sign In was successful, authenticate with Firebase
                    val account = task.getResult(ApiException::class.java)
                    account.idToken?.let { firebaseAuthWithGoogle(it, context as MainActivity) }
                } catch (e: ApiException) {
                    // Google Sign In failed, update UI appropriately
                }
            }
        }
    )




    val state = viewModel.state.collectAsState().value
    val errorState = viewModel.errorState.collectAsState().value
    val scope = rememberCoroutineScope()
    val intentSenderProvider: IntentSenderProvider = get()
    val intentProvider: IntentProvider = get()
    val openAlertDialog = remember { mutableStateOf(false) }

    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartIntentSenderForResult(),
        onResult = { result ->
            if (result.resultCode == RESULT_OK) {
                scope.launch {
                    intentProvider.setIntent(result.data)
                    viewModel.signInWithIntent()
                }
            }
        }
    )

    BackHandler {
        (context as MainActivity).finish()
    }

    if(state.isLoading) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(
                16.dp,
                alignment = Alignment.CenterHorizontally
            )
        ) {
            CircularProgressIndicator(color = MaterialTheme.colorScheme.secondary)
            Text(
                modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
                text = stringResource(R.string.login_login),
                style = MaterialTheme.typography.bodyLarge,
                textAlign = TextAlign.Center
            )
        }
    } else {
        if(state.logged) {
            navigateToMain()
        } else {
            LoginScreen(
                state = state,
                loading = false,
                signInWithDevice = {
                    viewModel.signInWithDevice(it)
                },
                login = {
                    scope.launch {
                        val signInIntentSender = intentSenderProvider.getIntentSender()
                        launcher.launch(
                            IntentSenderRequest.Builder(
                                signInIntentSender ?: return@launch
                            ).build()
                        )
                    }
                },
                signIn = {
                    val signInIntent = mGoogleSignInClient.signInIntent
                    val intentSender = PendingIntent.getActivities(context,RC_SIGN_IN,
                        arrayOf(signInIntent),FLAG_UPDATE_CURRENT).intentSender
                    launcher2.launch(IntentSenderRequest.Builder(intentSender).build())

                    //viewModel.signIn()
                }
            )
        }
    }
    when (errorState) {
        LoginError.LoginCancelled, LoginError.UnableToSignIn, LoginError.UserNotLogged, LoginError.UnableToDeleteUser, LoginError.UnableToLogout -> {
            AlertDialogError(
                description = {
                    when(errorState) {
                        LoginError.LoginCancelled -> Text(text = stringResource(R.string.error_login), color = MaterialTheme.colorScheme.secondary)
                        LoginError.NoError -> {}
                        LoginError.UnableToSignIn -> Text(text = stringResource(R.string.error_unable_login), color = MaterialTheme.colorScheme.secondary)
                        LoginError.UserNotLogged -> Text(text = stringResource(R.string.error_user_not_logged), color = MaterialTheme.colorScheme.secondary)
                        LoginError.UnableToDeleteUser -> Text(stringResource(R.string.error_delete_account), color = MaterialTheme.colorScheme.secondary)
                        LoginError.UnableToLogout -> Text(stringResource(R.string.error_logout_user), color = MaterialTheme.colorScheme.secondary)
                    }
                },
                onDismissRequest = {
                    viewModel.clearError()
                    openAlertDialog.value = false
                },
            )
        }
        LoginError.NoError -> {}
    }

}

private fun firebaseAuthWithGoogle(idToken: String, context: Activity) {
    val mAuth = FirebaseAuth.getInstance()
    val credential = GoogleAuthProvider.getCredential(idToken,  null)
    mAuth.signInWithCredential(credential)
        .addOnCompleteListener(context) { task ->
            if (task.isSuccessful) {
                // Sign in success, update UI with the signed-in user's information
                val user = mAuth.currentUser
                Toast.makeText(context,"Login existoso ${user?.uid}", Toast.LENGTH_SHORT).show()
                //updateUI(user)
            } else {
                // If sign in fails, display a message to the user.
                Toast.makeText(context, "Authentication Failed.", Toast.LENGTH_SHORT).show()
                //updateUI(null)
            }
        }
}

@Composable
fun LoginScreen(
    state: LoginState,
    loading: Boolean,
    signIn: () -> Unit,
    signInWithDevice: (String) -> Unit,
    login: () -> Unit = {},
) {
    val context = LocalContext.current
    LaunchedEffect(state) {
        login()
    }
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Brush.verticalGradient(colorStops = backgroundColor(isSystemInDarkTheme())))
                .verticalScroll(rememberScrollState())
                .padding(top = 24.dp),
            verticalArrangement = Arrangement.SpaceBetween,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(modifier = Modifier.size(30.dp))
            Column(
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    modifier = Modifier
                        .height(200.dp)
                        .padding(bottom = 40.dp)
                        .padding(horizontal = 24.dp),
                    painter = painterResource(id = R.drawable.championship_tapa),
                    contentDescription = null,
                    contentScale = ContentScale.FillHeight
                )
                Text(
                    modifier = Modifier.padding(start = 24.dp, end = 24.dp, bottom = 8.dp),
                    text = stringResource(R.string.login_title),
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = stringResource(R.string.login_description),
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Center
                )
                Text(
                    modifier = Modifier
                        .padding(24.dp)
                        .clickable {
                            val intent = Intent(ACTION_VIEW)
                            intent.setData(Uri.parse("https://destapalaslegumbres.es/reglamento/"))
                            startActivity(context, intent, null)
                        },
                    text = stringResource(R.string.legal_context),
                    style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.SemiBold),
                    textAlign = TextAlign.Center
                )
                if (loading) {

                } else {
                    /*
                    Button(
                        onClick = {
                            signIn()
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 36.dp, top = 16.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.ic_logo_google),
                            contentDescription = null
                        )
                        Text(
                            text = stringResource(R.string.login_with_google),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                     */
                    Button(
                        onClick = {
                            val uuid = Settings.Secure.getString(context.contentResolver, Settings.Secure.ANDROID_ID)
                            signInWithDevice(uuid)
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(start = 16.dp, end = 16.dp, bottom = 36.dp, top = 16.dp),
                        shape = RoundedCornerShape(30.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color.Black,
                            contentColor = Color.White
                        )
                    ) {
                        Text(
                            text = stringResource(R.string.press_to_continue),
                            modifier = Modifier.padding(6.dp)
                        )
                    }
                }
            }
            SocialWall()
        }
    }
}

@Preview(device = Devices.PIXEL_3A)
@Composable
fun LoginScreenPreview() {
    MyApplicationTheme {
        LoginScreen(
            state = LoginState(),
            signIn = {},
            signInWithDevice = {} ,
            login = {},
            loading = true
        )
    }
}

