package com.duyts.pokerhost.presentation.ui.components.alertDialog

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.domain.model.Player
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.amount
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.from
import pokerhost.composeapp.generated.resources.select_player
import pokerhost.composeapp.generated.resources.to
import pokerhost.composeapp.generated.resources.transfer
import pokerhost.composeapp.generated.resources.transfer_money

@Composable
fun TransferDialog(
	visible: Boolean,
	players: List<Player>,
	onDismiss: () -> Unit,
	onTransfer: (String, String, Float) -> Unit,
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
						onValueChange = {
							if (it.all { char -> char.isDigit() || char == '.' }) amountText = it
						},
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
