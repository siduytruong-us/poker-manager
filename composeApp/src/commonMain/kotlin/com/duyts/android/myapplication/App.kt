package com.duyts.android.myapplication

import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.di.create
import com.duyts.android.myapplication.presentation.graph.authGraph
import com.duyts.android.myapplication.presentation.graph.pokerDetailsGraph
import com.duyts.android.myapplication.presentation.graph.sessionListGraph
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.theme.AppTheme

@Composable
fun App() {
    val navController = rememberNavController()
    val component = remember { PokerComponent.create() }
    val settingsState by component.settingsViewModel.uiState.collectAsState()

    // Sử dụng 'key' để ép buộc Compose khởi tạo lại Resource System khi ngôn ngữ thay đổi
    key(settingsState.language) {
        AppTheme(darkTheme = settingsState.isDarkMode) {
            NavHost(
                navController = navController,
                startDestination = Route.Login,
            ) {
                authGraph(navController = navController, component = component)
                sessionListGraph(navController = navController, component = component)
                pokerDetailsGraph(navController = navController, component = component)
            }
        }
    }
}
