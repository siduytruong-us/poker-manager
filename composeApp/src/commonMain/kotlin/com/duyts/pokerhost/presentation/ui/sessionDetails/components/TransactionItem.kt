package com.duyts.pokerhost.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.Transaction
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.buy_in_format
import pokerhost.composeapp.generated.resources.cash_out_format
import pokerhost.composeapp.generated.resources.transfer_format

@Composable
fun TransactionItem(transaction: Transaction, players: List<Player>) {
	val player = players.find { it.id == transaction.playerId }
	val targetPlayer = players.find { it.id == transaction.targetPlayerId }

	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
	) {
		Row(
			modifier = Modifier.padding(16.dp),
			verticalAlignment = Alignment.CenterVertically,
			horizontalArrangement = Arrangement.SpaceBetween
		) {
			Column {
				val text = when (transaction.type) {
					TransactionType.BUY_IN -> stringResource(
						Res.string.buy_in_format,
						player?.name ?: ""
					)

					TransactionType.CASH_OUT -> stringResource(
						Res.string.cash_out_format,
						player?.name ?: ""
					)

					TransactionType.TRANSFER -> stringResource(
						Res.string.transfer_format,
						player?.name ?: "",
						targetPlayer?.name ?: ""
					)
				}
				Text(text, style = MaterialTheme.typography.bodyLarge)
				Text(transaction.type.name, style = MaterialTheme.typography.labelSmall)
			}
			Text(
				CurrencyUtils.format(transaction.amount),
				style = MaterialTheme.typography.titleMedium,
				color = when (transaction.type) {
					TransactionType.BUY_IN -> MaterialTheme.colorScheme.primary
					TransactionType.CASH_OUT -> MaterialTheme.colorScheme.secondary
					TransactionType.TRANSFER -> MaterialTheme.colorScheme.tertiary
				}
			)
		}
	}
}
