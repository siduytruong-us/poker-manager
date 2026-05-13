package com.duyts.android.myapplication.presentation.ui.sessionDetails.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.util.CurrencyUtils
import org.jetbrains.compose.resources.stringResource
import myapplication.composeapp.generated.resources.*

@Composable
fun SummaryCard(
    totalBuyIn: Float, 
    totalCashOut: Float, 
    smallBlind: Float, 
    bigBlind: Float,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.secondaryContainer)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = stringResource(Res.string.blinds_format, CurrencyUtils.format(smallBlind), CurrencyUtils.format(bigBlind)),
                style = MaterialTheme.typography.labelLarge,
                color = MaterialTheme.colorScheme.onSecondaryContainer.copy(alpha = 0.7f)
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(Res.string.total_buy_in), style = MaterialTheme.typography.labelMedium)
                    Text(CurrencyUtils.format(totalBuyIn), style = MaterialTheme.typography.titleMedium)
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(Res.string.total_cash_out), style = MaterialTheme.typography.labelMedium)
                    Text(CurrencyUtils.format(totalCashOut), style = MaterialTheme.typography.titleMedium)
                }
                val balance = totalCashOut - totalBuyIn
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(stringResource(Res.string.balance), style = MaterialTheme.typography.labelMedium)
                    Text(
                        CurrencyUtils.format(balance),
                        style = MaterialTheme.typography.titleMedium,
                        color = if (balance == 0f) MaterialTheme.colorScheme.onSurface 
                                else if (balance > 0f) MaterialTheme.colorScheme.primary
                                else MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}
