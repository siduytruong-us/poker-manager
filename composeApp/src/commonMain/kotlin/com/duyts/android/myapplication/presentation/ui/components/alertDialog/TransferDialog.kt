package com.duyts.android.myapplication.presentation.ui.components.alertDialog

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.model.Player
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

@Composable
fun TransferDialog(
    visible: Boolean,
    players: List<Player>,
    onDismiss: () -> Unit,
    onTransfer: (String, String, Float) -> Unit
) {
    if (visible) {
        var fromPlayerId by remember { mutableStateOf(players.firstOrNull()?.id ?: "") }
        var toPlayerId by remember { mutableStateOf(players.getOrNull(1)?.id ?: "") }
        var amountText by remember { mutableStateOf("1") }

        AlertDialog(
            onDismissRequest = onDismiss,
            title = { Text(stringResource(Res.string.transfer_money)) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(stringResource(Res.string.from))
                    PlayerSelector(players, fromPlayerId) { fromPlayerId = it }
                    Text(stringResource(Res.string.to))
                    PlayerSelector(players, toPlayerId) { toPlayerId = it }
                    TextField(
                        value = amountText,
                        onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amountText = it },
                        label = { Text(stringResource(Res.string.amount)) },
                        prefix = { Text("$") }
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val amount = amountText.toFloatOrNull() ?: 0f
                    if (fromPlayerId.isNotBlank() && toPlayerId.isNotBlank() && fromPlayerId != toPlayerId && amount > 0f) {
                        onTransfer(fromPlayerId, toPlayerId, amount)
                    }
                }) {
                    Text(stringResource(Res.string.transfer))
                }
            },
            dismissButton = {
                TextButton(onClick = onDismiss) { Text(stringResource(Res.string.cancel)) }
            }
        )
    }
}

@Composable
fun PlayerSelector(players: List<Player>, selectedId: String, onSelected: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val selectedPlayer = players.find { it.id == selectedId }

    Box {
        OutlinedButton(onClick = { expanded = true }, modifier = Modifier.fillMaxWidth()) {
            Text(selectedPlayer?.name ?: stringResource(Res.string.select_player))
        }
        DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            players.forEach { player ->
                DropdownMenuItem(
                    text = { Text(player.name) },
                    onClick = {
                        onSelected(player.id)
                        expanded = false
                    }
                )
            }
        }
    }
}
