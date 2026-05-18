package com.duyts.pokerhost.presentation.ui.splash

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.duyts.pokerhost.presentation.viewmodel.StartDestination
import com.duyts.pokerhost.presentation.viewmodel.StartViewModel

@Composable
fun SplashScreen(
	viewModel: StartViewModel,
	onNavigateToOnboarding: () -> Unit,
	onNavigateToLogin: () -> Unit,
	onNavigateToMain: () -> Unit,
) {
	val destination by viewModel.startDestination.collectAsState()

	LaunchedEffect(destination) {
		when (destination) {
			StartDestination.Onboarding -> onNavigateToOnboarding()
			StartDestination.Login -> onNavigateToLogin()
			StartDestination.Main -> onNavigateToMain()
			StartDestination.Checking -> { /* Do nothing, still checking */
			}
		}
	}

	Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
		CircularProgressIndicator()
	}
}
