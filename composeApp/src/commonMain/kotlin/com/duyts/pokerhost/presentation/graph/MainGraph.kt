package com.duyts.pokerhost.presentation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.duyts.pokerhost.di.PokerComponent
import com.duyts.pokerhost.presentation.navigation.Route
import com.duyts.pokerhost.presentation.ui.main.MainScreen

fun NavGraphBuilder.mainGraph(
	navController: NavController,
	component: PokerComponent,
) {
	composable<Route.Main>(
		deepLinks = listOf(
			navDeepLink<Route.Main>(basePath = "pokerhost://join")
		),
		enterTransition = { fadeIn() },
		exitTransition = {
			slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
		},
		popEnterTransition = {
			slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
		}
	) { backStackEntry ->
		val route: Route.Main = backStackEntry.toRoute()
		MainScreen(
			rootNavController = navController,
			component = component,
			sessionIdFromDeepLink = route.sessionId,
			onSessionClick = { id ->
				navController.navigate(Route.PokerSessionDetail(id))
			}
		)
	}
}
