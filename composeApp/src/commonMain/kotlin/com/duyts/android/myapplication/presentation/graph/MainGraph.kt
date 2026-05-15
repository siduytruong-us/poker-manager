package com.duyts.android.myapplication.presentation.graph

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.main.MainScreen

fun NavGraphBuilder.mainGraph(
    navController: NavController,
    component: PokerComponent
) {
    composable<Route.Main> {
        MainScreen(
            component = component,
            onSessionClick = { id ->
                navController.navigate(Route.PokerSessionDetail(id))
            }
        )
    }
}
