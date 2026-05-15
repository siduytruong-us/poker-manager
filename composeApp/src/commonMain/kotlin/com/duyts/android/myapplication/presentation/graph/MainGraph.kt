package com.duyts.android.myapplication.presentation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.toRoute
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.main.MainScreen

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    component: PokerComponent
) {
    composable<Route.Main>(
        deepLinks = listOf(
            navDeepLink {
                uriPattern = "poker://join/{sessionId}"
            },
            navDeepLink {
                uriPattern = "poker://join?sessionId={sessionId}"
            }
        )
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
