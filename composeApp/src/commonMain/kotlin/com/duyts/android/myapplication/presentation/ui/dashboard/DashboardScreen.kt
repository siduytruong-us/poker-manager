package com.duyts.android.myapplication.presentation.ui.dashboard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.QrCodeScanner
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.compose.LocalPlatformContext
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.SessionStatus
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.ui.components.profile.RecentSessionItem
import com.duyts.android.myapplication.presentation.viewmodel.PokerSessionListUiState
import com.duyts.android.myapplication.util.CurrencyUtils
import com.duyts.android.myapplication.util.toFloatOrZero
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.active_sessions
import myapplication.composeapp.generated.resources.app_name
import myapplication.composeapp.generated.resources.big_blind
import myapplication.composeapp.generated.resources.cancel
import myapplication.composeapp.generated.resources.complete_session
import myapplication.composeapp.generated.resources.complete_session_confirmation
import myapplication.composeapp.generated.resources.confirm
import myapplication.composeapp.generated.resources.create
import myapplication.composeapp.generated.resources.create_new_session
import myapplication.composeapp.generated.resources.delete
import myapplication.composeapp.generated.resources.delete_session
import myapplication.composeapp.generated.resources.delete_session_confirmation
import myapplication.composeapp.generated.resources.join_via_code
import myapplication.composeapp.generated.resources.live
import myapplication.composeapp.generated.resources.new_session
import myapplication.composeapp.generated.resources.recent_sessions
import myapplication.composeapp.generated.resources.resume
import myapplication.composeapp.generated.resources.session_title_optional
import myapplication.composeapp.generated.resources.small_blind
import myapplication.composeapp.generated.resources.stack
import myapplication.composeapp.generated.resources.view_all
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun DashboardScreen(
	state: PokerSessionListUiState,
	onSessionClick: (String) -> Unit,
	onCreateSession: (title: String?, smallBlind: Float, bigBlind: Float) -> Unit,
	onDeleteSession: (String) -> Unit,
	onCompleteSession: (String) -> Unit,
	onViewAllClick: () -> Unit,
) {
	var showCreateDialog by remember { mutableStateOf(false) }
	var sessionToDelete by remember { mutableStateOf<String?>(null) }
	var sessionToComplete by remember { mutableStateOf<String?>(null) }
	var sessionTitle by remember { mutableStateOf("") }
	var smallBlind by remember { mutableStateOf("5") }
	var bigBlind by remember { mutableStateOf("10") }

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					Row(verticalAlignment = Alignment.CenterVertically) {
						val userPhotoUrl = (state as? PokerSessionListUiState.Success)?.userPhotoUrl
						Surface(
							modifier = Modifier.size(32.dp),
							shape = CircleShape,
							color = MaterialTheme.colorScheme.surfaceVariant
						) {
							if (userPhotoUrl != null) {
								AsyncImage(
									model = ImageRequest.Builder(LocalPlatformContext.current)
										.data(userPhotoUrl)
										.crossfade(true)
										.build(),
									contentDescription = null,
									modifier = Modifier.fillMaxSize(),
									contentScale = ContentScale.Crop,
								)
							} else {
								Icon(
									imageVector = Icons.Default.Person,
									contentDescription = null,
									modifier = Modifier.padding(6.dp),
									tint = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}
						Spacer(Modifier.width(12.dp))
						Text(
							stringResource(Res.string.app_name),
							style = MaterialTheme.typography.titleLarge,
							fontWeight = FontWeight.Bold
						)
					}
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background
				)
			)
		}
	) { padding ->
		when (state) {
			is PokerSessionListUiState.Loading -> {
				Box(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}

			is PokerSessionListUiState.Error -> {
				Box(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentAlignment = Alignment.Center
				) {
					Text(state.message, color = MaterialTheme.colorScheme.error)
				}
			}

			is PokerSessionListUiState.Success -> {
				val allSessions = state.groupedSessions.values.flatten()
				val activeSessions = allSessions.filter { it.status == SessionStatus.ACTIVE }
				val completedSessions = allSessions.filter { it.status == SessionStatus.COMPLETED }
					.sortedByDescending { it.completedAt ?: it.createdAt }

				LazyColumn(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentPadding = PaddingValues(16.dp),
					verticalArrangement = Arrangement.spacedBy(24.dp)
				) {
					// Active Sessions Section
					if (activeSessions.isNotEmpty()) {
						item {
							Text(
								text = stringResource(Res.string.active_sessions),
								style = MaterialTheme.typography.headlineSmall,
								fontWeight = FontWeight.Bold
							)
							Spacer(Modifier.height(12.dp))
							activeSessions.forEach { session ->
								ActiveSessionCard(
									session = session,
									isOwner = session.ownerId == state.currentUserId,
									onClick = { onSessionClick(session.id) },
									onComplete = { sessionToComplete = session.id }
								)
								Spacer(Modifier.height(8.dp))
							}
						}
					}

					// Action Grid
					item {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.spacedBy(16.dp)
						) {
							ActionCard(
								title = stringResource(Res.string.create_new_session),
								icon = Icons.Default.Add,
								containerColor = MaterialTheme.colorScheme.primaryContainer,
								contentColor = MaterialTheme.colorScheme.onPrimaryContainer,
								modifier = Modifier.weight(1f),
								onClick = { showCreateDialog = true }
							)
							ActionCard(
								title = stringResource(Res.string.join_via_code),
								icon = Icons.Default.QrCodeScanner,
								containerColor = MaterialTheme.colorScheme.surfaceVariant,
								contentColor = MaterialTheme.colorScheme.onSurfaceVariant,
								modifier = Modifier.weight(1f),
								onClick = { /* Join via code */ }
							)
						}
					}

					// Recent Sessions Section
					item {
						Row(
							modifier = Modifier.fillMaxWidth(),
							horizontalArrangement = Arrangement.SpaceBetween,
							verticalAlignment = Alignment.CenterVertically
						) {
							Text(
								text = stringResource(Res.string.recent_sessions),
								style = MaterialTheme.typography.titleLarge,
								fontWeight = FontWeight.Bold
							)
							TextButton(onClick = onViewAllClick) {
								Text(stringResource(Res.string.view_all))
							}
						}
					}

					items(completedSessions.take(3), key = { it.id }) { session ->
						RecentSessionItem(
							session = session,
							currentUserId = state.currentUserId,
							onClick = { onSessionClick(session.id) }
						)
					}
				}
			}
		}
	}

	if (sessionToDelete != null) {
		AlertDialog(
			onDismissRequest = { sessionToDelete = null },
			title = { Text(stringResource(Res.string.delete_session)) },
			text = { Text(stringResource(Res.string.delete_session_confirmation)) },
			confirmButton = {
				Button(
					onClick = {
						sessionToDelete?.let { onDeleteSession(it) }
						sessionToDelete = null
					},
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
				) {
					Text(stringResource(Res.string.delete))
				}
			},
			dismissButton = {
				TextButton(onClick = { sessionToDelete = null }) {
					Text(stringResource(Res.string.cancel))
				}
			}
		)
	}

	if (sessionToComplete != null) {
		AlertDialog(
			onDismissRequest = { sessionToComplete = null },
			title = { Text(stringResource(Res.string.complete_session)) },
			text = { Text(stringResource(Res.string.complete_session_confirmation)) },
			confirmButton = {
				Button(
					onClick = {
						sessionToComplete?.let { onCompleteSession(it) }
						sessionToComplete = null
					},
					colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
				) {
					Text(stringResource(Res.string.confirm))
				}
			},
			dismissButton = {
				TextButton(onClick = { sessionToComplete = null }) {
					Text(stringResource(Res.string.cancel))
				}
			}
		)
	}

	if (showCreateDialog) {
		AlertDialog(
			onDismissRequest = { showCreateDialog = false },
			title = { Text(stringResource(Res.string.new_session)) },
			text = {
				Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
					TextField(
						value = sessionTitle,
						onValueChange = { sessionTitle = it },
						label = { Text(stringResource(Res.string.session_title_optional)) }
					)
					Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
						TextField(
							value = smallBlind,
							onValueChange = { input ->
								if (input.isEmpty() || (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1)) {
									smallBlind = input
								}
							},
							label = { Text(stringResource(Res.string.small_blind)) },
							modifier = Modifier.weight(1f),
							prefix = { Text("$") },
							singleLine = true
						)
						TextField(
							value = bigBlind,
							onValueChange = { input ->
								if (input.isEmpty() || (input.all { it.isDigit() || it == '.' } && input.count { it == '.' } <= 1)) {
									bigBlind = input
								}
							},
							label = { Text(stringResource(Res.string.big_blind)) },
							modifier = Modifier.weight(1f),
							prefix = { Text("$") },
							singleLine = true
						)
					}
				}
			},
			confirmButton = {
				Button(onClick = {
					val sb = smallBlind.toFloatOrZero()
					val bb = bigBlind.toFloatOrZero()
					onCreateSession(sessionTitle.ifBlank { null }, sb, bb)
					sessionTitle = ""
					showCreateDialog = false
				}) {
					Text(stringResource(Res.string.create))
				}
			},
			dismissButton = {
				TextButton(onClick = { showCreateDialog = false }) {
					Text(stringResource(Res.string.cancel))
				}
			}
		)
	}
}

@Composable
private fun ActiveSessionCard(
	session: PokerSession,
	isOwner: Boolean,
	onClick: () -> Unit,
	onComplete: () -> Unit,
) {
	var showMenu by remember { mutableStateOf(false) }

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick() },
		colors = CardDefaults.cardColors(
			containerColor = if (session.status == SessionStatus.COMPLETED)
				MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
			else
				MaterialTheme.colorScheme.surfaceVariant
		),
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		shape = MaterialTheme.shapes.large,
		border = BorderStroke(
			1.dp,
			MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.2f)
		)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(
					modifier = Modifier.weight(1f),
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(6.dp)
				) {
					Text(
						text = session.title,
						style = MaterialTheme.typography.headlineSmall,
						fontWeight = FontWeight.Bold,
						maxLines = 1,
						overflow = androidx.compose.ui.text.style.TextOverflow.Ellipsis
					)
					Surface(
						color = MaterialTheme.colorScheme.primaryContainer,
						shape = CircleShape
					) {
						Row(
							modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
							verticalAlignment = Alignment.CenterVertically
						) {
							Box(
								modifier = Modifier
									.size(6.dp)
									.clip(CircleShape)
									.background(MaterialTheme.colorScheme.primary)
							)
							Spacer(Modifier.width(4.dp))
							Text(
								text = stringResource(Res.string.live),
								style = MaterialTheme.typography.labelSmall,
								color = MaterialTheme.colorScheme.onPrimaryContainer
							)
						}
					}
				}

				if (isOwner) {
					Box {
						IconButton(onClick = { showMenu = true }) {
							Icon(Icons.Default.MoreVert, contentDescription = "More")
						}
						DropdownMenu(
							expanded = showMenu,
							onDismissRequest = { showMenu = false }
						) {
							DropdownMenuItem(
								text = { Text(stringResource(Res.string.complete_session)) },
								onClick = {
									showMenu = false
									onComplete()
								},
								leadingIcon = {
									Icon(
										Icons.Default.CheckCircle,
										contentDescription = null,
										tint = MaterialTheme.colorScheme.primary
									)
								}
							)
						}
					}
				}
			}

			Spacer(Modifier.height(8.dp))
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.Bottom
			) {
				Column {
					Text(
						text = stringResource(Res.string.stack),
						style = MaterialTheme.typography.labelLarge,
						color = MaterialTheme.colorScheme.onSurfaceVariant
					)
					Text(
						text = CurrencyUtils.format(session.totalBuyIn),
						style = MaterialTheme.typography.headlineMedium,
						fontWeight = FontWeight.Black
					)
				}
				Button(
					onClick = onClick,
					colors = ButtonDefaults.buttonColors(
						containerColor = MaterialTheme.colorScheme.primaryContainer,
						contentColor = MaterialTheme.colorScheme.onPrimaryContainer
					),
					contentPadding = PaddingValues(horizontal = 16.dp),
					shape = MaterialTheme.shapes.medium
				) {
					Text(stringResource(Res.string.resume))
					Spacer(Modifier.width(4.dp))
					Icon(Icons.AutoMirrored.Filled.KeyboardArrowRight, contentDescription = null)
				}
			}
		}
	}
}

@Composable
private fun ActionCard(
	title: String,
	icon: ImageVector,
	containerColor: Color,
	contentColor: Color,
	modifier: Modifier = Modifier,
	onClick: () -> Unit,
) {
	Card(
		modifier = modifier
			.height(140.dp)
			.clickable { onClick() },
		colors = CardDefaults.cardColors(
			containerColor = containerColor,
			contentColor = contentColor
		),
		shape = MaterialTheme.shapes.large,
		border = BorderStroke(
			1.dp,
			contentColor.copy(alpha = 0.1f)
		)
	) {
		Column(
			modifier = Modifier.fillMaxSize().padding(16.dp),
			horizontalAlignment = Alignment.CenterHorizontally,
			verticalArrangement = Arrangement.Center
		) {
			Surface(
				modifier = Modifier.size(48.dp),
				shape = CircleShape,
				color = contentColor.copy(alpha = 0.1f)
			) {
				Icon(
					imageVector = icon,
					contentDescription = null,
					modifier = Modifier.padding(12.dp)
				)
			}
			Spacer(Modifier.height(12.dp))
			Text(
				text = title,
				style = MaterialTheme.typography.labelLarge,
				fontWeight = FontWeight.Bold,
				textAlign = androidx.compose.ui.text.style.TextAlign.Center
			)
		}
	}
}

@Preview(showSystemUi = true)
@Composable
fun DashboardScreenPreview() {
	AppTheme(darkTheme = true) {
		DashboardScreen(
			state = PokerSessionListUiState.Success(
				groupedSessions = mapOf(
					"Today" to listOf(
						PokerSession(
							id = "ses.1",
							title = "High Stakes Hold'em",
							ownerId = "user1",
							status = SessionStatus.ACTIVE,
							players = listOf(
								Player(
									id = "user1",
									name = "Player 1",
									buyIn = 1000f,
									cashOut = 0f
								),
								Player(id = "ply.2", name = "Player 2", buyIn = 3250f, cashOut = 0f)
							)
						)
					),
					"Yesterday" to listOf(
						PokerSession(
							id = "ses.2",
							title = "Friday Night Cash",
							ownerId = "user1",
							status = SessionStatus.COMPLETED,
							createdAt = 1715000000000L,
							completedAt = 1715010000000L,
							players = listOf(
								Player(
									id = "user1",
									name = "Player 1",
									buyIn = 200f,
									cashOut = 650f
								),
								Player(id = "ply.3", name = "Player 3", buyIn = 100f, cashOut = 50f)
							)
						),
						PokerSession(
							id = "ses.3",
							title = "Home Game Tournament",
							ownerId = "user1",
							status = SessionStatus.COMPLETED,
							createdAt = 1714000000000L,
							completedAt = 1714010000000L,
							players = listOf(
								Player(id = "user1", name = "Player 1", buyIn = 50f, cashOut = 0f)
							)
						)
					)
				),
				currentUserId = "user1"
			),
			onSessionClick = {},
			onCreateSession = { _, _, _ -> },
			onDeleteSession = {},
			onCompleteSession = {},
			onViewAllClick = {}
		)
	}
}
