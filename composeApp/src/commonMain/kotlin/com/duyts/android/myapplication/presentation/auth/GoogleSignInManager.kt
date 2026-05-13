package com.duyts.android.myapplication.presentation.auth

import androidx.compose.runtime.Composable

@Composable
expect fun rememberGoogleSignInLauncher(
    onIdTokenReceived: (String) -> Unit,
    onError: (String) -> Unit
): GoogleSignInLauncher

interface GoogleSignInLauncher {
    fun launch()
}
