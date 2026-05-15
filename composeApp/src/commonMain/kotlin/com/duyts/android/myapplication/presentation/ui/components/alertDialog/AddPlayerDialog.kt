package com.duyts.android.myapplication.presentation.ui.components.alertDialog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun AddPlayerDialog(
    visible: Boolean,
    onDismissRequest: () -> Unit,
    onAddPlayer: (String) -> Unit
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
