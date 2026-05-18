package com.duyts.pokerhost.presentation.graph

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.duyts.pokerhost.di.PokerComponent
import com.duyts.pokerhost.presentation.navigation.Route
import com.duyts.pokerhost.presentation.ui.onboarding.OnboardingScreen
import com.duyts.pokerhost.presentation.ui.splash.SplashScreen

fun NavGraphBuilder.splashGraph(
	navController: NavController,
	component: PokerComponent,
) {
	composable<Route.Splash>(
		enterTransition = { fadeIn() },
		exitTransition = { fadeOut() }
	) {
		val startViewModel = viewModel { component.startViewModel }
		SplashScreen(
			viewModel = startViewModel,
			onNavigateToOnboarding = {
				navController.navigate(Route.Onboarding) {
					popUpTo(Route.Splash) { inclusive = true }
				}
			},
			onNavigateToLogin = {
				navController.navigate(Route.Login) {
					popUpTo(Route.Splash) { inclusive = true }
				}
			},
			onNavigateToMain = {
				navController.navigate(Route.Main()) {
					popUpTo(Route.Splash) { inclusive = true }
				}
			}
		)
	}

	composable<Route.Onboarding>(
		enterTransition = { fadeIn() },
		exitTransition = { fadeOut() }
	) {
		val startViewModel = viewModel { component.startViewModel }
		OnboardingScreen(
			onFinish = {
				startViewModel.completeOnboarding()
				navController.navigate(Route.Login) {
					popUpTo(Route.Onboarding) { inclusive = true }
				}
			}
		)
	}
}
