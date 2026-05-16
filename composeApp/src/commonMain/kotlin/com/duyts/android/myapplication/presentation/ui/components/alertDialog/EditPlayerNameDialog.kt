package com.duyts.android.myapplication.presentation.ui.components.alertDialog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditPlayerNameDialog(
    visible: Boolean,
    initialName: String,
    onDismissRequest: () -> Unit,
    onUpdateName: (String) -> Unit
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
