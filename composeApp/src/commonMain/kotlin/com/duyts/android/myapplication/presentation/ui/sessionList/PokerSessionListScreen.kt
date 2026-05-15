package com.duyts.android.myapplication.presentation.ui.sessionList

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.tooling.preview.Preview
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.viewmodel.PokerSessionListUiState
import com.duyts.android.myapplication.util.CurrencyUtils
import com.duyts.android.myapplication.util.toFloatOrZero
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun PokerSessionListScreen(
    state: PokerSessionListUiState,
    onSessionClick: (String) -> Unit,
    onCreateSession: (title: String?, smallBlind: Float, bigBlind: Float) -> Unit,
    onDeleteSession: (String) -> Unit
) {
    var showCreateDialog by remember { mutableStateOf(false) }
    var sessionToDelete by remember { mutableStateOf<String?>(null) }
    var sessionTitle by remember { mutableStateOf("") }
    var smallBlind by remember { mutableStateOf("5") }
    var bigBlind by remember { mutableStateOf("10") }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.poker_sessions)) },
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Default.Add, contentDescription = stringResource(Res.string.create_session))
            }
        }
    ) { padding ->
        when (state) {
            is PokerSessionListUiState.Loading -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }
            is PokerSessionListUiState.Error -> {
                Box(modifier = Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                    Text(state.message, color = MaterialTheme.colorScheme.error)
                }
            }
            is PokerSessionListUiState.Success -> {
                LazyColumn(
                    modifier = Modifier.fillMaxSize().padding(padding),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    state.groupedSessions.forEach { (date, sessions) ->
                        stickyHeader {
                            val displayDate = when(date) {
                                "Today" -> stringResource(Res.string.today)
                                "Yesterday" -> stringResource(Res.string.yesterday)
                                else -> date
                            }
                            Text(
                                text = displayDate,
                                style = MaterialTheme.typography.labelLarge,
                                color = MaterialTheme.colorScheme.primary,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .background(MaterialTheme.colorScheme.background)
                                    .padding(vertical = 8.dp)
                            )
                        }
                        
                        items(sessions) { session ->
                            SessionItem(
                                session = session,
                                isOwner = session.ownerId == state.currentUserId,
                                onClick = { onSessionClick(session.id) },
                                onDelete = { sessionToDelete = session.id }
                            )
                        }
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
private fun SessionItem(
    session: PokerSession,
    isOwner: Boolean = false,
    onClick: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(session.title, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(
                        Res.string.blinds_players_format,
                        CurrencyUtils.format(session.smallBlind),
                        CurrencyUtils.format(session.bigBlind),
                        session.players.size
                    ),
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            if (isOwner) {
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = stringResource(Res.string.delete_session))
                }
            }
        }
    }
}

@Preview(showSystemUi = true)
@Composable
fun PokerSessionListScreenPreview() {
    AppTheme {
        PokerSessionListScreen(
            state = PokerSessionListUiState.Success(
                groupedSessions = mapOf(
                    "Today" to listOf(
                        PokerSession(
                            id = "ses.1",
                            title = "Friday Night Poker",
                            ownerId = "user1",
                            players = listOf(
                                Player(id = "user1", name = "Player 1", buyIn = 5f, cashOut = 7f),
                                Player(id = "ply.2", name = "Player 2", buyIn = 5f, cashOut = 4f)
                            )
                        )
                    )
                ),
                currentUserId = "user1"
            ),
            onSessionClick = {},
            onCreateSession = { _, _, _ -> },
            onDeleteSession = {}
        )
    }
}
