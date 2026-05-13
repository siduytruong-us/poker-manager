package com.duyts.android.myapplication.presentation.graph

import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.sessionList.PokerSessionListScreen
import com.duyts.android.myapplication.presentation.ui.settings.SettingsScreen

fun NavGraphBuilder.sessionListGraph(
    navController: NavController,
    component: PokerComponent
) {
    composable<Route.PokerSessionList> {
        val viewModel = viewModel {
            component.pokerSessionListViewModel
        }
        val state by viewModel.uiState.collectAsState()

        PokerSessionListScreen(
            state = state,
            onSessionClick = { id ->
                navController.navigate(Route.PokerSessionDetail(id))
            },
            onCreateSession = { title, sb, bb ->
                viewModel.createSession(title, sb, bb)
            },
            onDeleteSession = { id ->
                viewModel.deleteSession(id)
            },
            onSettingsClick = {
                navController.navigate(Route.Settings)
            }
        )
    }

    composable<Route.Settings> {
        val viewModel = viewModel {
            component.settingsViewModel
        }
        val state by viewModel.uiState.collectAsState()

        SettingsScreen(
            state = state,
            onBack = { navController.popBackStack() },
            onDarkModeToggle = { viewModel.toggleDarkMode(it) },
            onLanguageChange = { viewModel.setLanguage(it) }
        )
    }
}
