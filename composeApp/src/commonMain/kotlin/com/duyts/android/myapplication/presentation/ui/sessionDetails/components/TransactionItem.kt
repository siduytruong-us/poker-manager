package com.duyts.android.myapplication.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.model.*
import com.duyts.android.myapplication.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

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
                    TransactionType.BUY_IN -> stringResource(Res.string.buy_in_format, player?.name ?: "")
                    TransactionType.CASH_OUT -> stringResource(Res.string.cash_out_format, player?.name ?: "")
                    TransactionType.TRANSFER -> stringResource(Res.string.transfer_format, player?.name ?: "", targetPlayer?.name ?: "")
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
