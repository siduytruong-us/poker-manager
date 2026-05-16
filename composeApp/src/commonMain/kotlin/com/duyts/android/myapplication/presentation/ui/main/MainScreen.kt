package com.duyts.android.myapplication.presentation.ui.main

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.NavController
import androidx.navigation.NavDestination.Companion.hasRoute
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.lifecycle.viewmodel.compose.viewModel
import com.duyts.android.myapplication.di.PokerComponent
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.core.Result
import com.duyts.android.myapplication.presentation.navigation.Route
import com.duyts.android.myapplication.presentation.ui.profile.EditProfileScreen
import com.duyts.android.myapplication.presentation.ui.profile.ProfileScreen
import com.duyts.android.myapplication.presentation.ui.sessionList.PokerSessionListScreen
import com.duyts.android.myapplication.presentation.ui.settings.SettingsScreen
import com.duyts.android.myapplication.presentation.ui.statistics.StatisticsScreen
import com.duyts.android.myapplication.util.CurrencyUtils
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.poker_sessions
import myapplication.composeapp.generated.resources.profile
import myapplication.composeapp.generated.resources.settings
import myapplication.composeapp.generated.resources.cancel
import myapplication.composeapp.generated.resources.join
import myapplication.composeapp.generated.resources.join_session
import myapplication.composeapp.generated.resources.statistics
import org.jetbrains.compose.resources.stringResource

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
			Route.PokerSessionList,
			Icons.AutoMirrored.Filled.List,
			stringResource(Res.string.poker_sessions)
		),
		BottomNavItem(
			Route.Statistics,
			Icons.Default.BarChart,
			stringResource(Res.string.statistics)
		),
		BottomNavItem(Route.Profile, Icons.Default.Person, stringResource(Res.string.profile))
	)

	Scaffold(
		bottomBar = {
			NavigationBar {
				items.forEach { item ->
					val selected =
						currentDestination?.hierarchy?.any { it.hasRoute(item.route::class) } == true
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
			startDestination = Route.PokerSessionList,
			modifier = Modifier.padding(innerPadding).consumeWindowInsets(innerPadding)
		) {
			composable<Route.PokerSessionList> {
				val viewModel = viewModel {
					component.pokerSessionListViewModel
				}
				val state by viewModel.uiState.collectAsState()

				PokerSessionListScreen(
					state = state,
					onSessionClick = onSessionClick,
					onCreateSession = { title, sb, bb ->
						viewModel.createSession(title, sb, bb)
					},
					onDeleteSession = { id ->
						viewModel.deleteSession(id)
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

			composable<Route.Statistics> {
				val viewModel = viewModel {
					component.statisticsViewModel
				}
				val state by viewModel.uiState.collectAsState()

				StatisticsScreen(
					state = state
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
