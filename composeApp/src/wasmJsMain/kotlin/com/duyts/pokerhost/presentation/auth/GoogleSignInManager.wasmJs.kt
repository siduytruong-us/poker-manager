package com.duyts.pokerhost.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(clientId, callback) => { google.accounts.id.initialize({ client_id: clientId, callback: (response) => callback(response.credential) }); google.accounts.id.prompt(); }")
external fun googleSignIn(clientId: String, callback: (String) -> Unit)

@Composable
actual fun rememberGoogleSignInLauncher(
	onIdTokenReceived: (String) -> Unit,
	onError: (String) -> Unit,
): GoogleSignInLauncher {
	val clientId = "556290841007-ehbtbldvi00l84kpq0hb29j2gd49nlkf.apps.googleusercontent.com"

	return remember {
		object : GoogleSignInLauncher {
			override fun launch() {
				try {
					googleSignIn(clientId) { credential ->
						if (credential.isNotEmpty()) {
							onIdTokenReceived(credential)
						} else {
							onError("No credential received from Google")
						}
					}
				} catch (e: Exception) {
					onError(e.message ?: "Unknown error during Google Sign In")
				}
			}
		}
	}
}
