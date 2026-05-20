package com.duyts.pokerhost.presentation.ui.sessionDetails

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Archive
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material.icons.filled.Unarchive
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SmallFloatingActionButton
import androidx.compose.material3.Surface
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.fake.FakeData
import com.duyts.pokerhost.presentation.theme.AppTheme
import com.duyts.pokerhost.presentation.ui.components.alertDialog.AddPlayerDialog
import com.duyts.pokerhost.presentation.ui.components.alertDialog.EditPlayerNameDialog
import com.duyts.pokerhost.presentation.ui.components.alertDialog.EditTitleDialog
import com.duyts.pokerhost.presentation.ui.components.alertDialog.TransferDialog
import com.duyts.pokerhost.presentation.ui.components.modalBottomSheet.HistoryBottomSheet
import com.duyts.pokerhost.presentation.ui.sessionDetails.components.PlayerAmountBottomSheet
import com.duyts.pokerhost.presentation.ui.sessionDetails.components.PlayerItem
import com.duyts.pokerhost.presentation.ui.sessionDetails.components.SummaryCard
import com.duyts.pokerhost.presentation.viewmodel.PokerSessionDetailUiState
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.add_player
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.complete_session
import pokerhost.composeapp.generated.resources.complete_session_confirmation
import pokerhost.composeapp.generated.resources.confirm
import pokerhost.composeapp.generated.resources.edit
import pokerhost.composeapp.generated.resources.history
import pokerhost.composeapp.generated.resources.players
import pokerhost.composeapp.generated.resources.session_detail
import pokerhost.composeapp.generated.resources.transfer

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PokerSessionDetailScreen(
	state: PokerSessionDetailUiState,
	onBack: () -> Unit,
	onBuyIn: (playerId: String, amount: Float) -> Unit,
	onCashOut: (playerId: String, amount: Float) -> Unit,
	onAddPlayer: (name: String) -> Unit,
	onTransfer: (fromId: String, toId: String, amount: Float) -> Unit,
	onUpdateTitle: (title: String) -> Unit,
	onUpdatePlayerName: (playerId: String, name: String) -> Unit,
	onUpdatePlayerArchiveStatus: (playerId: String, isArchived: Boolean) -> Unit,
	onCompleteSession: () -> Unit,
	onShareSession: (title: String) -> Unit,
) {
	var showAddPlayerDialog by remember { mutableStateOf(false) }
	var showTransferDialog by remember { mutableStateOf(false) }
	var showEditTitleDialog by remember { mutableStateOf(false) }
	var showHistory by remember { mutableStateOf(false) }
	var showArchived by remember { mutableStateOf(false) }
	var showFabMenu by remember { mutableStateOf(false) }
	var showCompleteConfirmation by remember { mutableStateOf(false) }
	var editingPlayer by remember { mutableStateOf<Player?>(null) }

	var showPlayerAmountSheet by remember { mutableStateOf<Pair<Player, TransactionType>?>(null) }

	val sheetState = rememberModalBottomSheetState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					val successState = state as? PokerSessionDetailUiState.Success
					val title =
						successState?.session?.title ?: stringResource(Res.string.session_detail)
					val isOwner = successState?.isOwner == true
					val isFinished = successState?.session?.status == SessionStatus.COMPLETED

					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.clickable(enabled = isOwner && !isFinished) {
							showEditTitleDialog = true
						}
					) {
						Column {
							Row(verticalAlignment = Alignment.CenterVertically) {
								Text(title)
								if (isOwner) {
									Spacer(Modifier.width(4.dp))
									Icon(
										imageVector = Icons.Default.Edit,
										contentDescription = stringResource(Res.string.edit),
										modifier = Modifier.size(18.dp),
										tint = MaterialTheme.colorScheme.primary
									)
								}
							}
							successState?.session?.status?.let { status ->
								Surface(
									color = if (status == SessionStatus.ACTIVE)
										MaterialTheme.colorScheme.primaryContainer
									else
										MaterialTheme.colorScheme.surfaceVariant,
									shape = MaterialTheme.shapes.extraSmall,
									modifier = Modifier.padding(top = 2.dp)
								) {
									Text(
										text = status.name,
										style = MaterialTheme.typography.labelSmall,
										modifier = Modifier.padding(horizontal = 4.dp),
										color = if (status == SessionStatus.ACTIVE)
											MaterialTheme.colorScheme.onPrimaryContainer
										else
											MaterialTheme.colorScheme.onSurfaceVariant
									)
								}
							}
						}
					}
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(
							Icons.AutoMirrored.Filled.ArrowBack,
							contentDescription = stringResource(Res.string.cancel)
						) // Back same as cancel in some context or add 'back'
					}
				},
				actions = {
					val successState = state as? PokerSessionDetailUiState.Success
					val isOwner = successState?.isOwner == true
					val isFinished = successState?.session?.status == SessionStatus.COMPLETED

					if (isOwner && !isFinished) {
						IconButton(onClick = { showCompleteConfirmation = true }) {
							Icon(
								Icons.Default.CheckCircle,
								contentDescription = "Complete Session",
								tint = MaterialTheme.colorScheme.primary
							)
						}
					}
					IconButton(
						onClick = {
							val successState = state as? PokerSessionDetailUiState.Success
							successState?.session?.title?.let { onShareSession(it) }
						},
						enabled = state is PokerSessionDetailUiState.Success
					) {
						Icon(
							Icons.Default.Share,
							contentDescription = "Share Session"
						)
					}
				}
			)
		},
		floatingActionButton = {
			val successState = state as? PokerSessionDetailUiState.Success
			val isFinished = successState?.session?.status == SessionStatus.COMPLETED

			if (successState != null && successState.isOwner && !isFinished) {
				Column(
					horizontalAlignment = Alignment.End,
					verticalArrangement = Arrangement.spacedBy(16.dp)
				) {
					AnimatedVisibility(
						visible = showFabMenu,
						enter = fadeIn() + expandVertically(),
						exit = fadeOut() + shrinkVertically()
					) {
						Column(
							horizontalAlignment = Alignment.End,
							verticalArrangement = Arrangement.spacedBy(16.dp)
						) {
							// History Action
							SmallFloatingActionButton(
								onClick = {
									showHistory = true
									showFabMenu = false
								},
								containerColor = MaterialTheme.colorScheme.secondaryContainer,
								contentColor = MaterialTheme.colorScheme.onSecondaryContainer
							) {
								Row(
									modifier = Modifier.padding(horizontal = 12.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(Icons.Default.History, contentDescription = null)
									Spacer(Modifier.width(8.dp))
									Text(stringResource(Res.string.history))
								}
							}

							// Transfer Action
							SmallFloatingActionButton(
								onClick = {
									showTransferDialog = true
									showFabMenu = false
								},
								containerColor = MaterialTheme.colorScheme.secondaryContainer,
								contentColor = MaterialTheme.colorScheme.onSecondaryContainer
							) {
								Row(
									modifier = Modifier.padding(horizontal = 12.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(Icons.Default.SwapHoriz, contentDescription = null)
									Spacer(Modifier.width(8.dp))
									Text(stringResource(Res.string.transfer))
								}
							}

							// Add Player Action
							SmallFloatingActionButton(
								onClick = {
									showAddPlayerDialog = true
									showFabMenu = false
								},
								containerColor = MaterialTheme.colorScheme.secondaryContainer,
								contentColor = MaterialTheme.colorScheme.onSecondaryContainer
							) {
								Row(
									modifier = Modifier.padding(horizontal = 12.dp),
									verticalAlignment = Alignment.CenterVertically
								) {
									Icon(Icons.Default.PersonAdd, contentDescription = null)
									Spacer(Modifier.width(8.dp))
									Text(stringResource(Res.string.add_player))
								}
							}
						}
					}

					FloatingActionButton(
						onClick = { showFabMenu = !showFabMenu },
						containerColor = MaterialTheme.colorScheme.primary,
						contentColor = MaterialTheme.colorScheme.onPrimary
					) {
						Icon(
							imageVector = Icons.Default.Style,
							contentDescription = "Menu"
						)
					}
				}
			}
		}
	) { padding ->
		when (state) {
			is PokerSessionDetailUiState.Loading -> {
				Box(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentAlignment = Alignment.Center
				) {
					CircularProgressIndicator()
				}
			}

			is PokerSessionDetailUiState.Error -> {
				Box(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentAlignment = Alignment.Center
				) {
					Text(state.message, color = MaterialTheme.colorScheme.error)
				}
			}

			is PokerSessionDetailUiState.Success -> {
				val currentSession = state.session
				LazyColumn(
					modifier = Modifier.fillMaxSize().padding(padding),
					contentPadding = PaddingValues(16.dp),
					verticalArrangement = Arrangement.spacedBy(8.dp)
				) {
					item {
						SummaryCard(
							totalBuyIn = currentSession.totalBuyIn,
							totalCashOut = currentSession.totalCashOut,
							smallBlind = currentSession.smallBlind,
							bigBlind = currentSession.bigBlind
						)
					}

					item {
						Text(
							text = stringResource(Res.string.players),
							style = MaterialTheme.typography.titleLarge,
							modifier = Modifier.padding(top = 8.dp, bottom = 4.dp)
						)
					}

					val (archivedPlayers, activePlayers) = currentSession.players.partition { it.isArchived }
					val isFinished = currentSession.status == SessionStatus.COMPLETED

					items(activePlayers, key = { it.id }) { player ->
						if (state.isOwner && !isFinished) {
							val dismissState = rememberSwipeToDismissBoxState()

							LaunchedEffect(dismissState.currentValue) {
								if (dismissState.currentValue == SwipeToDismissBoxValue.EndToStart) {
									onUpdatePlayerArchiveStatus(player.id, true)
									dismissState.snapTo(SwipeToDismissBoxValue.Settled)
								}
							}

							SwipeToDismissBox(
								state = dismissState,
								enableDismissFromStartToEnd = false,
								modifier = Modifier.animateItem(),
								backgroundContent = {
									val color = when (dismissState.dismissDirection) {
										SwipeToDismissBoxValue.EndToStart -> MaterialTheme.colorScheme.errorContainer
										else -> MaterialTheme.colorScheme.surface
									}
									Box(
										modifier = Modifier
											.fillMaxSize()
											.background(color)
											.padding(horizontal = 20.dp),
										contentAlignment = Alignment.CenterEnd
									) {
										Icon(
											Icons.Default.Archive,
											contentDescription = "Archive",
											tint = MaterialTheme.colorScheme.onErrorContainer
										)
									}
								}
							) {
								PlayerItem(
									player = player,
									isOwner = state.isOwner,
									isSessionActive = !isFinished,
									onBuyIn = {
										showPlayerAmountSheet = player to TransactionType.BUY_IN
									},
									onCashOut = {
										showPlayerAmountSheet = player to TransactionType.CASH_OUT
									},
									onEditName = { editingPlayer = player },
									onToggleArchive = {
										onUpdatePlayerArchiveStatus(
											player.id,
											true
										)
									}
								)
							}
						} else {
							PlayerItem(
								player = player,
								isOwner = state.isOwner,
								isSessionActive = !isFinished,
								modifier = Modifier.animateItem(),
								onBuyIn = {
									showPlayerAmountSheet = player to TransactionType.BUY_IN
								},
								onCashOut = {
									showPlayerAmountSheet = player to TransactionType.CASH_OUT
								},
								onEditName = { editingPlayer = player },
								onToggleArchive = { onUpdatePlayerArchiveStatus(player.id, true) }
							)
						}
					}

					if (archivedPlayers.isNotEmpty()) {
						item {
							Row(
								modifier = Modifier
									.fillMaxWidth()
									.clickable { showArchived = !showArchived }
									.padding(top = 16.dp, bottom = 8.dp),
								verticalAlignment = Alignment.CenterVertically,
								horizontalArrangement = Arrangement.SpaceBetween
							) {
								Text(
									text = "Archived Players (${archivedPlayers.size})",
									style = MaterialTheme.typography.titleMedium,
									color = MaterialTheme.colorScheme.onSurfaceVariant
								)
								Icon(
									imageVector = if (showArchived) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
									contentDescription = if (showArchived) "Collapse" else "Expand",
									tint = MaterialTheme.colorScheme.onSurfaceVariant
								)
							}
						}

						item {
							AnimatedVisibility(
								visible = showArchived,
								enter = expandVertically(),
								exit = shrinkVertically()
							) {
								Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
									archivedPlayers.forEach { player ->
										if (state.isOwner && !isFinished) {
											val dismissState = rememberSwipeToDismissBoxState()

											LaunchedEffect(dismissState.currentValue) {
												if (dismissState.currentValue == SwipeToDismissBoxValue.StartToEnd) {
													onUpdatePlayerArchiveStatus(player.id, false)
													dismissState.snapTo(SwipeToDismissBoxValue.Settled)
												}
											}

											SwipeToDismissBox(
												state = dismissState,
												enableDismissFromEndToStart = false,
												modifier = Modifier.animateItem(),
												backgroundContent = {
													val color =
														when (dismissState.dismissDirection) {
															SwipeToDismissBoxValue.StartToEnd -> MaterialTheme.colorScheme.primaryContainer
															else -> MaterialTheme.colorScheme.surface
														}
													Box(
														modifier = Modifier
															.fillMaxSize()
															.background(color)
															.padding(horizontal = 20.dp),
														contentAlignment = Alignment.CenterStart
													) {
														Icon(
															Icons.Default.Unarchive,
															contentDescription = "Unarchive",
															tint = MaterialTheme.colorScheme.onPrimaryContainer
														)
													}
												}
											) {
												PlayerItem(
													player = player,
													isOwner = state.isOwner,
													isSessionActive = !isFinished,
													onBuyIn = {},
													onCashOut = {},
													onEditName = { editingPlayer = player },
													onToggleArchive = {
														onUpdatePlayerArchiveStatus(
															player.id,
															false
														)
													}
												)
											}
										} else {
											PlayerItem(
												player = player,
												isOwner = state.isOwner,
												isSessionActive = !isFinished,
												modifier = Modifier.animateItem(),
												onBuyIn = {},
												onCashOut = {},
												onEditName = { editingPlayer = player },
												onToggleArchive = {
													onUpdatePlayerArchiveStatus(
														player.id,
														false
													)
												}
											)
										}
									}
								}
							}
						}
					}
				}

				showPlayerAmountSheet?.let { playerAmount ->
					PlayerAmountBottomSheet(
						player = playerAmount.first,
						type = playerAmount.second,
						suggestions = state.playerAmountSuggestion,
						onDismiss = { showPlayerAmountSheet = null },
						onConfirm = { amount ->
							val (player, type) = playerAmount
							if (type == TransactionType.BUY_IN) {
								onBuyIn(player.id, amount)
							} else {
								onCashOut(player.id, amount)
							}
							showPlayerAmountSheet = null
						}
					)
				}

				HistoryBottomSheet(
					visible = showHistory,
					transactions = currentSession.transactions,
					players = currentSession.players,
					sheetState = sheetState,
					onDismissRequest = { showHistory = false }
				)

				AddPlayerDialog(
					visible = showAddPlayerDialog,
					onDismissRequest = { showAddPlayerDialog = false },
					onAddPlayer = { name ->
						onAddPlayer(name)
						showAddPlayerDialog = false
					}
				)

				TransferDialog(
					visible = showTransferDialog,
					players = currentSession.players,
					onDismiss = { showTransferDialog = false },
					onTransfer = { fromId, toId, amount ->
						onTransfer(fromId, toId, amount)
						showTransferDialog = false
					}
				)

				EditTitleDialog(
					visible = showEditTitleDialog,
					initialTitle = currentSession.title,
					onDismissRequest = { showEditTitleDialog = false },
					onUpdateTitle = { title ->
						onUpdateTitle(title)
						showEditTitleDialog = false
					}
				)

				EditPlayerNameDialog(
					visible = editingPlayer != null,
					initialName = editingPlayer?.name ?: "",
					onDismissRequest = { editingPlayer = null },
					onUpdateName = { newName ->
						editingPlayer?.let { onUpdatePlayerName(it.id, newName) }
						editingPlayer = null
					}
				)

				if (showCompleteConfirmation) {
					AlertDialog(
						onDismissRequest = { showCompleteConfirmation = false },
						title = { Text(stringResource(Res.string.complete_session)) },
						text = { Text(stringResource(Res.string.complete_session_confirmation)) },
						confirmButton = {
							Button(
								onClick = {
									onCompleteSession()
									showCompleteConfirmation = false
								},
								colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
							) {
								Text(stringResource(Res.string.confirm))
							}
						},
						dismissButton = {
							TextButton(onClick = { showCompleteConfirmation = false }) {
								Text(stringResource(Res.string.cancel))
							}
						}
					)
				}
			}
		}
	}
}

@Preview
@Composable
fun PokerSessionDetailScreenPreview() {
	AppTheme {
		PokerSessionDetailScreen(
			state = PokerSessionDetailUiState.Success(
				session = FakeData.detailSession
			),
			onBack = {},
			onBuyIn = { _, _ -> },
			onCashOut = { _, _ -> },
			onAddPlayer = {},
			onTransfer = { _, _, _ -> },
			onUpdateTitle = {},
			onUpdatePlayerName = { _, _ -> },
			onUpdatePlayerArchiveStatus = { _, _ -> },
			onCompleteSession = {},
			onShareSession = {}
		)
	}
}
