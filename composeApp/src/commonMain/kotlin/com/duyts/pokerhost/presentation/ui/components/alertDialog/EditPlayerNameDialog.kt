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
import pokerhost.composeapp.generated.resources.cancel
import pokerhost.composeapp.generated.resources.confirm
import pokerhost.composeapp.generated.resources.player_name

@Composable
fun EditPlayerNameDialog(
	visible: Boolean,
	initialName: String,
	onDismissRequest: () -> Unit,
	onUpdateName: (String) -> Unit,
) {
	if (visible) {
		var playerName by remember { mutableStateOf(initialName) }

		AlertDialog(
			onDismissRequest = onDismissRequest,
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
						onUpdateName(playerName)
						onDismissRequest()
					}
				}) {
					Text(stringResource(Res.string.confirm))
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
