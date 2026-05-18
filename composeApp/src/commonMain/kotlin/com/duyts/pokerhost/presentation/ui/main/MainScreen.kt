package com.duyts.pokerhost.presentation.ui.main

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.di.PokerComponent
import com.duyts.pokerhost.presentation.navigation.Route
import com.duyts.pokerhost.presentation.ui.dashboard.DashboardScreen
import com.duyts.pokerhost.presentation.ui.profile.EditProfileScreen
import com.duyts.pokerhost.presentation.ui.profile.ProfileScreen
import com.duyts.pokerhost.presentation.ui.settings.SettingsScreen
import com.duyts.pokerhost.presentation.ui.statistics.StatisticsScreen
import com.duyts.pokerhost.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.dashboard
import pokerhost.composeapp.generated.resources.join
import pokerhost.composeapp.generated.resources.join_session
import pokerhost.composeapp.generated.resources.profile
import pokerhost.composeapp.generated.resources.statistics

data class BottomNavItem(
	val route: Route,
	val icon: ImageVector,
	val label: String,
)

@Composable
fun MainScreen(
	rootNavController: NavController,
	component: PokerComponent,
	sessionIdFromDeepLink: String? = null,
	onSessionClick: (String) -> Unit,
) {
	val navController = rememberNavController()
	val navBackStackEntry by navController.currentBackStackEntryAsState()
	val currentDestination = navBackStackEntry?.destination

	val mainViewModel = viewModel { component.mainViewModel }
	val sessionToJoin by mainViewModel.sessionToJoin.collectAsState()

	LaunchedEffect(sessionIdFromDeepLink) {
		mainViewModel.handleDeepLink(sessionIdFromDeepLink)
	}

	val items = listOf(
		BottomNavItem(
			Route.Dashboard,
			Icons.Default.Dashboard,
			stringResource(Res.string.dashboard)
		),
		BottomNavItem(
			Route.Statistics(),
			Icons.Default.BarChart,
			stringResource(Res.string.statistics)
		),
		BottomNavItem(Route.Profile, Icons.Default.Person, stringResource(Res.string.profile))
	)

	Scaffold(
		bottomBar = {
			NavigationBar {
				items.forEach { item ->
					val selected = if (item.route is Route.Statistics) {
						currentDestination?.hierarchy?.any { it.hasRoute(Route.Statistics::class) } == true
					} else {
						currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
					}
					NavigationBarItem(
						icon = { Icon(item.icon, contentDescription = item.label) },
						label = { Text(item.label) },
						selected = selected,
						onClick = {
							navController.navigate(item.route) {
								popUpTo(navController.graph.findStartDestination().id) {
									saveState = true
								}
								launchSingleTop = true
								restoreState = true
							}
						}
					)
				}
			}
		},
		contentWindowInsets = WindowInsets(0, 0, 0, 0)
	) { innerPadding ->
		NavHost(
			navController = navController,
			startDestination = Route.Dashboard,
			modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
		) {
			composable<Route.Dashboard> {
				val viewModel = viewModel {
					component.dashboardViewModel
				}
				val state by viewModel.uiState.collectAsState()
				val sessionCreated by viewModel.sessionCreated.collectAsState()

				LaunchedEffect(sessionCreated) {
					sessionCreated?.let { id ->
						onSessionClick(id)
						viewModel.resetSessionCreated()
					}
				}

				DashboardScreen(
					state = state,
					onSessionClick = onSessionClick,
					onCreateSession = { title, sb, bb ->
						viewModel.createSession(title, sb, bb)
					},
					onDeleteSession = { id ->
						viewModel.deleteSession(id)
					},
					onCompleteSession = { id ->
						viewModel.completeSession(id)
					},
					onViewAllClick = {
						navController.navigate(Route.Statistics(scrollToHistory = true)) {
							popUpTo(navController.graph.findStartDestination().id) {
								saveState = true
							}
							launchSingleTop = true
							restoreState = true
						}
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
					onBack = {
						navController.navigateUp()
					},
					onDarkModeToggle = { viewModel.toggleDarkMode(it) },
					onLanguageChange = { viewModel.setLanguage(it) }
				)
			}

			composable<Route.Statistics> { backStackEntry ->
				val route: Route.Statistics = backStackEntry.toRoute()
				val viewModel = viewModel {
					component.statisticsViewModel
				}
				val state by viewModel.uiState.collectAsState()

				StatisticsScreen(
					state = state,
					scrollToHistory = route.scrollToHistory
				)
			}

			composable<Route.Profile> {
				val viewModel = viewModel {
					component.profileViewModel
				}
				val state by viewModel.uiState.collectAsState()

				ProfileScreen(
					state = state,
					onEditProfile = {
						navController.navigate(Route.EditProfile)
					},
					onSettings = {
						navController.navigate(Route.Settings)
					},
					onLogout = {
						viewModel.logout()
						rootNavController.navigate(Route.Login) {
							popUpTo(0) { inclusive = true }
						}
					}
				)
			}

			composable<Route.EditProfile> {
				val viewModel = viewModel {
					component.editProfileViewModel
				}
				val user by viewModel.user.collectAsState()
				val state by viewModel.uiState.collectAsState()

				EditProfileScreen(
					user = user,
					state = state,
					onSave = { name, url, bytes -> viewModel.updateProfile(name, url, bytes) },
					onBack = { navController.navigateUp() },
					onResetState = { viewModel.resetState() }
				)
			}
		}
	}

	if (sessionToJoin != null) {
		AlertDialog(
			onDismissRequest = { mainViewModel.onDismissJoinDialog() },
			title = { Text(stringResource(Res.string.join_session)) },
			text = {
				Column {
					Text("Title: ${sessionToJoin?.title}")
					Text(
						"Blinds: ${CurrencyUtils.format(sessionToJoin?.smallBlind ?: 0f)} / ${
							CurrencyUtils.format(
								sessionToJoin?.bigBlind ?: 0f
							)
						}"
					)
				}
			},
			confirmButton = {
				Button(onClick = {
					sessionToJoin?.id?.let { id ->
						mainViewModel.joinSession(id) { result ->
							if (result is Result.Success) {
								mainViewModel.onDismissJoinDialog()
								rootNavController.navigate(Route.PokerSessionDetail(id)) {
									popUpTo<Route.Main> { inclusive = false }
								}
							}
						}
					}
				}) {
					Text(stringResource(Res.string.join))
				}
			},
			dismissButton = {
				TextButton(onClick = { mainViewModel.onDismissJoinDialog() }) {
					Text(stringResource(Res.string.cancel))
				}
			}
		)
	}
}
