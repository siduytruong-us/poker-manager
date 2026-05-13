package com.duyts.android.myapplication.presentation.ui.sessionDetails

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.SwapHoriz
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.Transaction
import com.duyts.android.myapplication.domain.model.TransactionType
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.ui.sessionDetails.components.*
import com.duyts.android.myapplication.presentation.viewmodel.PokerSessionDetailUiState
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

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
) {
	var showAddPlayerDialog by remember { mutableStateOf(false) }
	var showTransferDialog by remember { mutableStateOf(false) }
	var showEditTitleDialog by remember { mutableStateOf(false) }
	var showHistory by remember { mutableStateOf(false) }
	var playerName by remember { mutableStateOf("") }
	var sessionTitle by remember { mutableStateOf("") }

	var showPlayerAmountSheet by remember { mutableStateOf<Pair<Player, TransactionType>?>(null) }

	val sheetState = rememberModalBottomSheetState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					val title = (state as? PokerSessionDetailUiState.Success)?.session?.title
						?: stringResource(Res.string.session_detail)
					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.clickable(enabled = state is PokerSessionDetailUiState.Success) {
							sessionTitle = title
							showEditTitleDialog = true
						}
					) {
						Text(title)
						if (state is PokerSessionDetailUiState.Success) {
							Spacer(Modifier.width(4.dp))
							Icon(
								imageVector = Icons.Default.Edit,
								contentDescription = stringResource(Res.string.edit),
								modifier = Modifier.size(18.dp),
								tint = MaterialTheme.colorScheme.primary
							)
						}
					}
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.cancel)) // Back same as cancel in some context or add 'back'
					}
				},
				actions = {
					IconButton(
						onClick = { showHistory = true },
						enabled = state is PokerSessionDetailUiState.Success
					) {
						Icon(Icons.Default.History, contentDescription = stringResource(Res.string.history))
					}
					IconButton(
						onClick = { showTransferDialog = true },
						enabled = state is PokerSessionDetailUiState.Success
					) {
						Icon(Icons.Default.SwapHoriz, contentDescription = stringResource(Res.string.transfer))
					}
				}
			)
		},
		floatingActionButton = {
			if (state is PokerSessionDetailUiState.Success) {
				FloatingActionButton(onClick = { showAddPlayerDialog = true }) {
					Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.add_player))
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

					items(currentSession.players) { player ->
						PlayerItem(
							player = player,
							onBuyIn = { showPlayerAmountSheet = player to TransactionType.BUY_IN },
							onCashOut = {
								showPlayerAmountSheet = player to TransactionType.CASH_OUT
							}
						)
					}
				}

				if (showPlayerAmountSheet != null) {
					PlayerAmountBottomSheet(
						player = showPlayerAmountSheet!!.first,
						type = showPlayerAmountSheet!!.second,
						suggestions = state.playerAmountSuggestion,
						onDismiss = { showPlayerAmountSheet = null },
						onConfirm = { amount ->
							val (player, type) = showPlayerAmountSheet!!
							if (type == TransactionType.BUY_IN) {
								onBuyIn(player.id, amount)
							} else {
								onCashOut(player.id, amount)
							}
							showPlayerAmountSheet = null
						}
					)
				}

				if (showHistory) {
					ModalBottomSheet(
						onDismissRequest = { showHistory = false },
						sheetState = sheetState
					) {
						LazyColumn(
							modifier = Modifier.fillMaxWidth().padding(16.dp),
							verticalArrangement = Arrangement.spacedBy(8.dp)
						) {
							item {
								Text(
									text = stringResource(Res.string.history),
									style = MaterialTheme.typography.headlineSmall,
									modifier = Modifier.padding(bottom = 8.dp)
								)
							}
							items(currentSession.transactions.reversed()) { transaction ->
								TransactionItem(transaction, currentSession.players)
							}
							item {
								Spacer(modifier = Modifier.height(32.dp))
							}
						}
					}
				}

				if (showAddPlayerDialog) {
					AlertDialog(
						onDismissRequest = { showAddPlayerDialog = false },
						title = { Text(stringResource(Res.string.add_player)) },
						text = {
							TextField(
								value = playerName,
								onValueChange = { playerName = it },
								label = { Text(stringResource(Res.string.player_name)) }
							)
						},
						confirmButton = {
							Button(onClick = {
								if (playerName.isNotBlank()) {
									onAddPlayer(playerName)
									playerName = ""
									showAddPlayerDialog = false
								}
							}) {
								Text(stringResource(Res.string.add))
							}
						},
						dismissButton = {
							TextButton(onClick = { showAddPlayerDialog = false }) {
								Text(stringResource(Res.string.cancel))
							}
						}
					)
				}

				if (showTransferDialog) {
					TransferDialog(
						players = currentSession.players,
						onDismiss = { showTransferDialog = false },
						onTransfer = { fromId, toId, amount ->
							onTransfer(fromId, toId, amount)
							showTransferDialog = false
						}
					)
				}

				if (showEditTitleDialog) {
					AlertDialog(
						onDismissRequest = { showEditTitleDialog = false },
						title = { Text(stringResource(Res.string.edit_session_title)) },
						text = {
							TextField(
								value = sessionTitle,
								onValueChange = { sessionTitle = it },
							)
						},
						confirmButton = {
							Button(onClick = {
								if (sessionTitle.isNotBlank()) {
									onUpdateTitle(sessionTitle)
									showEditTitleDialog = false
								}
							}) {
								Text(stringResource(Res.string.confirm))
							}
						},
						dismissButton = {
							TextButton(onClick = { showEditTitleDialog = false }) {
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
	val mockPlayers = listOf(
		Player(id = "ply.1", name = "Duy", buyIn = 5f, cashOut = 7f),
		Player(id = "ply.2", name = "John", buyIn = 5f, cashOut = 4f),
		Player(id = "ply.3", name = "Alice", buyIn = 10f, cashOut = 12f)
	)
	val mockSession = PokerSession(
		id = "ses.1",
		title = "Friday Night Poker",
		players = mockPlayers,
		transactions = listOf(
			Transaction(
				id = "trx.1",
				type = TransactionType.BUY_IN,
				amount = 5f,
				playerId = "ply.1"
			),
			Transaction(
				id = "trx.2",
				type = TransactionType.CASH_OUT,
				amount = 7f,
				playerId = "ply.1"
			),
			Transaction(
				id = "trx.3",
				type = TransactionType.TRANSFER,
				amount = 1f,
				playerId = "ply.2",
				targetPlayerId = "ply.3"
			)
		)
	)

	AppTheme {
		PokerSessionDetailScreen(
			state = PokerSessionDetailUiState.Success(
				session = mockSession
			),
			onBack = {},
			onBuyIn = { _, _ -> },
			onCashOut = { _, _ -> },
			onAddPlayer = {},
			onTransfer = { _, _, _ -> },
			onUpdateTitle = {}
		)
	}
}
