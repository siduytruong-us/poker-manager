package com.duyts.pokerhost.presentation.graph

import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.duyts.pokerhost.di.PokerComponent
import com.duyts.pokerhost.presentation.navigation.Route
import com.duyts.pokerhost.presentation.ui.login.LoginScreen
import com.duyts.pokerhost.presentation.ui.public.PublicInfoScreen
import com.duyts.pokerhost.util.PublicContent

fun NavGraphBuilder.authGraph(
	navController: NavController,
	component: PokerComponent,
) {
	composable<Route.Login>(
		enterTransition = { fadeIn() },
		exitTransition = { fadeOut() }
	) {
		val viewModel = viewModel {
			component.loginViewModel
		}
		val state by viewModel.loginState.collectAsState()

		LoginScreen(
			state = state,
			onLoginSuccess = {
				navController.navigate(Route.Main()) {
					popUpTo(Route.Login) { inclusive = true }
				}
			},
			onSignInSuccess = { idToken, accessToken ->
				viewModel.onGoogleSignInSuccess(idToken, accessToken)
			},
			onNavigateToPrivacy = {
				navController.navigate(Route.Privacy)
			},
			onNavigateToTerms = {
				navController.navigate(Route.Terms)
			}
		)
	}

	composable<Route.Privacy>(
		enterTransition = { fadeIn() },
		exitTransition = { fadeOut() }
	) {
		PublicInfoScreen(
			title = "Privacy Policy",
			content = PublicContent.PRIVACY_POLICY,
			onBack = { navController.popBackStack() }
		)
	}

	composable<Route.Terms>(
		enterTransition = { fadeIn() },
		exitTransition = { fadeOut() }
	) {
		PublicInfoScreen(
			title = "Terms & Conditions",
			content = PublicContent.TERMS_CONDITIONS,
			onBack = { navController.popBackStack() }
		)
	}
}
