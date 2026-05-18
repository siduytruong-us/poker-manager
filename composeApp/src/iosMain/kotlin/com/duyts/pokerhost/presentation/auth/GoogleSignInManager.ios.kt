package com.duyts.pokerhost.presentation.auth

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember

object GoogleSignInProvider {
	var delegate: ((onIdTokenReceived: (String) -> Unit, onError: (String) -> Unit) -> Unit)? = null
}

@Composable
actual fun rememberGoogleSignInLauncher(
	onIdTokenReceived: (String) -> Unit,
	onError: (String) -> Unit,
): GoogleSignInLauncher {
	return remember {
		object : GoogleSignInLauncher {
			override fun launch() {
				val delegate = GoogleSignInProvider.delegate
				if (delegate != null) {
					delegate(onIdTokenReceived, onError)
				} else {
					onError("Google Sign In delegate not set. Please set GoogleSignInProvider.delegate in your iOS app.")
				}
			}
		}
	}
}

