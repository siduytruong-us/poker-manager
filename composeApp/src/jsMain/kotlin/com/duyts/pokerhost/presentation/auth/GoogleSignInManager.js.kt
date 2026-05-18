package com.duyts.pokerhost.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

@JsName("google")
external object Google {
	val accounts: Accounts
}

external interface Accounts {
	val id: Id
}

external interface Id {
	fun initialize(config: dynamic)
	fun prompt()
}

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
					val config = js("{}")
					config.client_id = clientId
					config.callback = { response: dynamic ->
						val credential = response.credential as? String
						if (credential != null) {
							onIdTokenReceived(credential)
						} else {
							onError("No credential received from Google")
						}
					}

					Google.accounts.id.initialize(config)
					Google.accounts.id.prompt()
				} catch (e: Exception) {
					onError(e.message ?: "Unknown error during Google Sign In")
				}
			}
		}
	}
}
