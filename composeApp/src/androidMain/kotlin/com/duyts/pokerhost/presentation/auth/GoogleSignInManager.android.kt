package com.duyts.pokerhost.presentation.auth

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException

@Composable
actual fun rememberGoogleSignInLauncher(
	onIdTokenReceived: (String) -> Unit,
	onError: (String) -> Unit,
): GoogleSignInLauncher {
	val context = LocalContext.current

	val gso = remember {
		GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
			.requestEmail()
			.requestIdToken("1044534759993-a8fs00o0te89paknaqjqc7ree61ue3j3.apps.googleusercontent.com")
			.build()
	}

	val googleSignInClient = remember { GoogleSignIn.getClient(context, gso) }

	val launcher = rememberLauncherForActivityResult(
		contract = ActivityResultContracts.StartActivityForResult()
	) { result ->
		val task = GoogleSignIn.getSignedInAccountFromIntent(result.data)
		try {
			val account = task.getResult(ApiException::class.java)
			account.idToken?.let { onIdTokenReceived(it) } ?: onError("ID Token is null")
		} catch (e: ApiException) {
			onError(e.message ?: "Sign in failed")
		}
	}

	return remember {
		object : GoogleSignInLauncher {
			override fun launch() {
				launcher.launch(googleSignInClient.signInIntent)
			}
		}
	}
}
