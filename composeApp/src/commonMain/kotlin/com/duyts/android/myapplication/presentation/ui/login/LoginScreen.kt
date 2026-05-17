package com.duyts.android.myapplication.presentation.ui.login

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.presentation.auth.rememberGoogleSignInLauncher
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.viewmodel.LoginState
import kotlinx.coroutines.delay
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.img_login_background
import myapplication.composeapp.generated.resources.sign_in_google
import myapplication.composeapp.generated.resources.terms_conditions
import myapplication.composeapp.generated.resources.welcome_description
import myapplication.composeapp.generated.resources.welcome_message
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.resources.stringResource

@Composable
fun LoginScreen(
    state: LoginState,
    onLoginSuccess: () -> Unit,
	onSignInSuccess: (idToken: String) -> Unit,
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
			delay(1_000)
			onLoginSuccess()
		} else if (state is LoginState.Error) {
			snackbarHostState.showSnackbar(state.message)
		}
	}

	Scaffold(
		snackbarHost = { SnackbarHost(snackbarHostState) },
		containerColor = Color.Transparent
	) { padding ->
		Box(modifier = Modifier.fillMaxSize()) {
			Image(
				painter = painterResource(Res.drawable.img_login_background),
				contentDescription = null,
				modifier = Modifier.fillMaxSize(),
				contentScale = ContentScale.Crop
			)

			// Optional overlay to ensure text readability
			Surface(
				modifier = Modifier.fillMaxSize(),
				color = Color.Black.copy(alpha = 0.75f)
			) {

				Column(
					modifier = Modifier
						.fillMaxSize()
						.padding(padding)
						.padding(24.dp),
					horizontalAlignment = Alignment.CenterHorizontally,
					verticalArrangement = Arrangement.Center
				) {
					Text(
						text = stringResource(Res.string.welcome_message),
						style = MaterialTheme.typography.headlineLarge,
						fontWeight = FontWeight.Bold,
						color = Color.White
					)

					Spacer(modifier = Modifier.height(8.dp))

					Text(
						text = stringResource(Res.string.welcome_description),
						style = MaterialTheme.typography.bodyMedium,
						color = Color.White.copy(alpha = 0.8f)
					)

					Spacer(modifier = Modifier.height(48.dp))

					if (state is LoginState.Loading || state is LoginState.Checking) {
						CircularProgressIndicator(color = Color.White)
					} else {
						Button(
							onClick = { googleSignInLauncher.launch() },
							modifier = Modifier
								.fillMaxWidth()
								.height(56.dp),
							shape = MaterialTheme.shapes.medium,
							colors = ButtonDefaults.buttonColors(
								containerColor = MaterialTheme.colorScheme.primary,
								contentColor = MaterialTheme.colorScheme.onPrimary
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
						color = Color.White.copy(alpha = 0.6f)
					)
				}
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
