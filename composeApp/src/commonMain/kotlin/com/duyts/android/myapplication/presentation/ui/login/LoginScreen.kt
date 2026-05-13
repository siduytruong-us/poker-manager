package com.duyts.android.myapplication.presentation.ui.login

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.auth.rememberGoogleSignInLauncher
import com.duyts.android.myapplication.presentation.viewmodel.LoginState
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

@Composable
fun LoginScreen(
    state: LoginState,
    onLoginSuccess: () -> Unit,
    onSignInSuccess: (idToken: String) -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    
    val googleSignInLauncher = rememberGoogleSignInLauncher(
        onIdTokenReceived = { idToken ->
            onSignInSuccess(idToken)
        },
        onError = { error ->
            print(error)
            // Show error
        }
    )

    LaunchedEffect(state) {
        if (state is LoginState.Success) {
            onLoginSuccess()
        } else if (state is LoginState.Error) {
            snackbarHostState.showSnackbar(state.message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { padding ->
        Surface(
            modifier = Modifier.fillMaxSize().padding(padding),
            color = MaterialTheme.colorScheme.background
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = stringResource(Res.string.welcome_message),
                    style = MaterialTheme.typography.headlineLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
                
                Spacer(modifier = Modifier.height(8.dp))
                
                Text(
                    text = stringResource(Res.string.welcome_description),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                Spacer(modifier = Modifier.height(48.dp))

                if (state is LoginState.Loading || state is LoginState.Checking) {
                    CircularProgressIndicator()
                } else {
                    Button(
                        onClick = { googleSignInLauncher.launch() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(56.dp),
                        shape = MaterialTheme.shapes.medium,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.secondaryContainer,
                            contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Text(
                                text = "G",
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Black,
                                modifier = Modifier.padding(end = 12.dp)
                            )
                            Text(
                                text = stringResource(Res.string.sign_in_google),
                                style = MaterialTheme.typography.titleMedium
                            )
                        }
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = stringResource(Res.string.terms_conditions),
                    style = MaterialTheme.typography.labelSmall,
                    color = MaterialTheme.colorScheme.outline
                )
            }
        }
    }
}

@Preview
@Composable
fun LoginScreenPreview() {
    AppTheme {
        LoginScreen(
            state = LoginState.Idle,
            onLoginSuccess = {},
            onSignInSuccess = {}
        )
    }
}
