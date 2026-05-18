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

@Composable
fun EditTitleDialog(
	visible: Boolean,
	initialTitle: String,
	onDismissRequest: () -> Unit,
	onUpdateTitle: (String) -> Unit,
) {
	if (visible) {
		var sessionTitle by remember { mutableStateOf(initialTitle) }

		AlertDialog(
			onDismissRequest = onDismissRequest,
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
