package com.duyts.android.myapplication

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.remember
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.rememberNavController
import coil3.ImageLoader
import coil3.compose.setSingletonImageLoaderFactory
import coil3.network.ktor3.KtorNetworkFetcherFactory
import coil3.request.crossfade
import coil3.util.DebugLogger
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.di.create
import com.duyts.android.myapplication.presentation.graph.authGraph
import com.duyts.android.myapplication.presentation.graph.mainGraph
import com.duyts.android.myapplication.presentation.graph.pokerDetailsGraph
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.theme.AppTheme

@Composable
fun App() {
    setSingletonImageLoaderFactory { context ->
        ImageLoader.Builder(context)
            .components {
                add(KtorNetworkFetcherFactory())
            }
            .crossfade(true)
            .logger(DebugLogger())
            .build()
    }

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
                mainGraph(navController = navController, component = component)
                pokerDetailsGraph(navController = navController, component = component)
            }
        }
    }
}
