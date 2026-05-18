package com.duyts.pokerhost.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.amount
import pokerhost.composeapp.generated.resources.buy_in
import pokerhost.composeapp.generated.resources.cash_out
import pokerhost.composeapp.generated.resources.confirm

@OptIn(ExperimentalMaterial3Api::class, ExperimentalLayoutApi::class)
@Composable
fun PlayerAmountBottomSheet(
	player: Player,
	type: TransactionType,
	suggestions: List<Float>,
	onDismiss: () -> Unit,
	onConfirm: (Float) -> Unit,
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
				onValueChange = {
					if (it.all { char -> char.isDigit() || char == '.' }) amountText = it
				},
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
