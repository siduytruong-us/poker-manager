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
import com.duyts.android.myapplication.presentation.ui.components.alertDialog.*
import com.duyts.android.myapplication.presentation.ui.components.modalBottomSheet.HistoryBottomSheet
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

	var showPlayerAmountSheet by remember { mutableStateOf<Pair<Player, TransactionType>?>(null) }

	val sheetState = rememberModalBottomSheetState()

	Scaffold(
		topBar = {
			TopAppBar(
				title = {
					val successState = state as? PokerSessionDetailUiState.Success
					val title = successState?.session?.title ?: stringResource(Res.string.session_detail)
					val isOwner = successState?.isOwner == true

					Row(
						verticalAlignment = Alignment.CenterVertically,
						modifier = Modifier.clickable(enabled = isOwner) {
							showEditTitleDialog = true
						}
					) {
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
				},
				navigationIcon = {
					IconButton(onClick = onBack) {
						Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = stringResource(Res.string.cancel)) // Back same as cancel in some context or add 'back'
					}
				},
				actions = {
					val isOwner = (state as? PokerSessionDetailUiState.Success)?.isOwner == true
					IconButton(
						onClick = { showHistory = true },
						enabled = state is PokerSessionDetailUiState.Success
					) {
						Icon(Icons.Default.History, contentDescription = stringResource(Res.string.history))
					}
					if (isOwner) {
						IconButton(
							onClick = { showTransferDialog = true }
						) {
							Icon(Icons.Default.SwapHoriz, contentDescription = stringResource(Res.string.transfer))
						}
					}
				}
			)
		},
		floatingActionButton = {
			val successState = state as? PokerSessionDetailUiState.Success
			if (successState != null && successState.isOwner) {
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
							isOwner = state.isOwner,
							onBuyIn = { showPlayerAmountSheet = player to TransactionType.BUY_IN },
							onCashOut = {
								showPlayerAmountSheet = player to TransactionType.CASH_OUT
							}
						)
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
