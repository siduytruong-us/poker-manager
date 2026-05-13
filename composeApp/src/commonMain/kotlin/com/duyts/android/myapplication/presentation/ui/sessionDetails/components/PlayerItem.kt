package com.duyts.android.myapplication.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

@Composable
fun PlayerItem(
    player: Player,
    onBuyIn: () -> Unit,
    onCashOut: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(player.name, style = MaterialTheme.typography.titleLarge)
                Text(
                    text = stringResource(Res.string.profit_format, CurrencyUtils.format(player.netProfit)),
                    color = if (player.netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = stringResource(
                    Res.string.player_stats_format,
                    CurrencyUtils.format(player.buyIn),
                    CurrencyUtils.format(player.cashOut),
                    CurrencyUtils.format(player.adjustment)
                ),
                style = MaterialTheme.typography.bodySmall
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(
                    onClick = onBuyIn,
                    modifier = Modifier.weight(1f),
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) { Text(stringResource(Res.string.buy_in)) }
                Button(
                    onClick = onCashOut,
                    modifier = Modifier.weight(1f),
                ) { Text(stringResource(Res.string.cash_out)) }
            }
        }
    }
}
