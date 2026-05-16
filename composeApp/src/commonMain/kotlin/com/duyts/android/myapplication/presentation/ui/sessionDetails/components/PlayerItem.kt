package com.duyts.android.myapplication.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.util.CurrencyUtils
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.buy_in
import myapplication.composeapp.generated.resources.cash_out
import myapplication.composeapp.generated.resources.player_stats_format
import myapplication.composeapp.generated.resources.profit_format
import org.jetbrains.compose.resources.stringResource

@Composable
fun PlayerItem(
    player: Player,
    isOwner: Boolean = false,
    isSessionActive: Boolean = true,
    modifier: Modifier = Modifier,
    onBuyIn: () -> Unit,
    onCashOut: () -> Unit,
    onEditName: () -> Unit,
    onToggleArchive: () -> Unit
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = if (player.isArchived) 
                MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
            else 
                MaterialTheme.colorScheme.surfaceVariant
        )
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(player.name, style = MaterialTheme.typography.titleLarge)
                    if (isOwner && isSessionActive) {
                        IconButton(onClick = onEditName, modifier = Modifier.size(24.dp)) {
                            Icon(
                                imageVector = Icons.Default.Edit,
                                contentDescription = "Edit Name",
                                modifier = Modifier.size(16.dp)
                            )
                        }
                    }
                }
                
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = stringResource(Res.string.profit_format, CurrencyUtils.format(player.netProfit)),
                        color = if (player.netProfit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                    )
                }
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
            
            if (isOwner && isSessionActive && !player.isArchived) {
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
}

@Preview
@Composable
fun PlayerItemPreview() {
    AppTheme {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text("Active - Owner")
            PlayerItem(
                player = Player(id = "1", name = "Duy Truong", buyIn = 100f, cashOut = 150f),
                isOwner = true,
                onBuyIn = {},
                onCashOut = {},
                onEditName = {},
                onToggleArchive = {}
            )

            Text("Active - Guest")
            PlayerItem(
                player = Player(id = "2", name = "John Doe", buyIn = 50f, cashOut = 20f),
                isOwner = false,
                onBuyIn = {},
                onCashOut = {},
                onEditName = {},
                onToggleArchive = {}
            )

            Text("Archived")
            PlayerItem(
                player = Player(id = "3", name = "Alice", buyIn = 200f, cashOut = 200f, isArchived = true),
                isOwner = true,
                onBuyIn = {},
                onCashOut = {},
                onEditName = {},
                onToggleArchive = {}
            )
        }
    }
}
