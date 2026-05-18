package com.duyts.pokerhost.presentation.ui.components.alertDialog

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.add
import pokerhost.composeapp.generated.resources.add_player
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.player_name

@Composable
fun AddPlayerDialog(
	visible: Boolean,
	onDismissRequest: () -> Unit,
	onAddPlayer: (String) -> Unit,
) {
	if (visible) {
		var playerName by remember { mutableStateOf("") }

		AlertDialog(
			onDismissRequest = onDismissRequest,
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
						onDismissRequest()
					}
				}) {
					Text(stringResource(Res.string.add))
				}
			},
			dismissButton = {
				TextButton(onClick = onDismissRequest) {
					Text(stringResource(Res.string.cancel))
				}
			}
		)
	}
}
