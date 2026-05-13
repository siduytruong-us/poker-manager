package com.duyts.android.myapplication.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.domain.model.TransactionType
import com.duyts.android.myapplication.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlayerAmountBottomSheet(
    player: Player,
    type: TransactionType,
    suggestions: List<Float>,
    onDismiss: () -> Unit,
    onConfirm: (Float) -> Unit
) {
    var amountText by remember { mutableStateOf("") }

    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            val title = if (type == TransactionType.BUY_IN) {
                stringResource(Res.string.buy_in)
            } else {
                stringResource(Res.string.cash_out)
            }
            Text(
                text = "$title - ${player.name}",
                style = MaterialTheme.typography.headlineSmall
            )

            OutlinedTextField(
                value = amountText,
                onValueChange = { if (it.all { char -> char.isDigit() || char == '.' }) amountText = it },
                label = { Text(stringResource(Res.string.amount)) },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                prefix = { Text("$") }
            )

            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                suggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = { amountText = suggestion.toString() },
                        label = { Text(CurrencyUtils.format(suggestion)) }
                    )
                }
            }

            Button(
                onClick = {
                    val amount = amountText.toFloatOrNull() ?: 0f
                    if (amount > 0f) onConfirm(amount)
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = amountText.isNotBlank() && (amountText.toFloatOrNull() ?: 0f) > 0f
            ) {
                Text(stringResource(Res.string.confirm))
            }
        }
    }
}
