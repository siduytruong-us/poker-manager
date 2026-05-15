package com.duyts.android.myapplication.presentation.ui.components.alertDialog

import androidx.compose.material3.*
import androidx.compose.runtime.*
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.*
import org.jetbrains.compose.resources.stringResource

@Composable
fun EditTitleDialog(
    visible: Boolean,
    initialTitle: String,
    onDismissRequest: () -> Unit,
    onUpdateTitle: (String) -> Unit
) {
    if (visible) {
        var sessionTitle by remember { mutableStateOf(initialTitle) }

        AlertDialog(
            onDismissRequest = onDismissRequest,
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
