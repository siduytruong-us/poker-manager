package com.duyts.android.myapplication.presentation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.login.LoginScreen

fun NavGraphBuilder.authGraph(
    navController: NavController,
    component: PokerComponent
) {
    composable<Route.Login> {
        val viewModel = viewModel {
            component.loginViewModel
        }
        val state by viewModel.loginState.collectAsState()

        LoginScreen(
            state = state,
            onLoginSuccess = {
                navController.navigate(Route.PokerSessionList) {
                    popUpTo(Route.Login) { inclusive = true }
                }
            },
            onSignInSuccess = { idToken ->
                viewModel.onGoogleSignInSuccess(idToken)
            }
        )
    }
}
