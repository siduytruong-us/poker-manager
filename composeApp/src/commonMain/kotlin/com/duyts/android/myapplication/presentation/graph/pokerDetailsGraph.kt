package com.duyts.android.myapplication.presentation.graph

import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import androidx.navigation.toRoute
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.sessionDetails.PokerSessionDetailScreen

fun NavGraphBuilder.pokerDetailsGraph(
    navController: NavController,
    component: PokerComponent
) {
    navigation<Route.PokerSessionGraph>(
        startDestination = Route.PokerSessionDetail::class
    ) {
        composable<Route.PokerSessionDetail>(
            enterTransition = {
                slideInHorizontally(initialOffsetX = { it }, animationSpec = tween(300))
            },
            exitTransition = {
                slideOutHorizontally(targetOffsetX = { -it }, animationSpec = tween(300))
            },
            popEnterTransition = {
                slideInHorizontally(initialOffsetX = { -it }, animationSpec = tween(300))
            },
            popExitTransition = {
                slideOutHorizontally(targetOffsetX = { it }, animationSpec = tween(300))
            }
        ) { backStackEntry ->
            val route: Route.PokerSessionDetail = backStackEntry.toRoute()
            val viewModel = viewModel {
                component.pokerSessionDetailViewModelFactory(route.sessionId)
            }
            val state by viewModel.uiState.collectAsState()

            PokerSessionDetailScreen(
                state = state,
                onBack = { navController.popBackStack() },
                onBuyIn = { playerId, amount -> viewModel.buyIn(playerId, amount) },
                onCashOut = { playerId, amount -> viewModel.cashOut(playerId, amount) },
                onAddPlayer = { name -> viewModel.addPlayer(name) },
                onTransfer = { fromId, toId, amount ->
                    viewModel.transfer(fromId, toId, amount)
                },
                onUpdateTitle = { title -> viewModel.updateTitle(title) },
                onUpdatePlayerName = { id, name -> viewModel.updatePlayerName(id, name) },
                onUpdatePlayerArchiveStatus = { id, archived -> viewModel.updatePlayerArchiveStatus(id, archived) },
                onCompleteSession = { viewModel.completeSession() }
            )
        }
    }
}
