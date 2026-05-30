package com.duyts.pokerhost.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(clientId, callback) => { google.accounts.id.initialize({ client_id: clientId, callback: (response) => callback(response.credential) }); google.accounts.id.prompt(); }")
external fun googleSignIn(clientId: String, callback: (String) -> Unit)

@Composable
actual fun rememberGoogleSignInLauncher(
	onTokenReceived: (idToken: String, accessToken: String?) -> Unit,
	onError: (String) -> Unit,
): GoogleSignInLauncher {
	val clientId = WEB_GOOGLE_CLIENT_ID

	return remember {
		object : GoogleSignInLauncher {
			override fun launch() {
				try {
					googleSignIn(clientId) { credential ->
						if (credential.isNotEmpty()) {
							onTokenReceived(credential, null)
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
