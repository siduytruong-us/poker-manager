package com.duyts.pokerhost.presentation.ui.components.modalBottomSheet

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.Transaction
import com.duyts.pokerhost.presentation.ui.sessionDetails.components.TransactionItem
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.history
import pokerhost.composeapp.generated.resources.no_transactions

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HistoryBottomSheet(
	visible: Boolean,
	transactions: List<Transaction>,
	players: List<Player>,
	sheetState: SheetState,
	onDismissRequest: () -> Unit,
) {
	if (visible) {
		ModalBottomSheet(
			onDismissRequest = onDismissRequest,
			sheetState = sheetState
		) {
			LazyColumn(
				modifier = Modifier.fillMaxWidth().padding(16.dp),
				verticalArrangement = Arrangement.spacedBy(8.dp)
			) {
				item {
					Text(
						text = stringResource(Res.string.history),
						style = MaterialTheme.typography.headlineSmall,
						modifier = Modifier.padding(bottom = 8.dp)
					)
				}
				if (transactions.isEmpty()) {
					item {
						Box(
							modifier = Modifier.fillMaxWidth().padding(vertical = 32.dp),
							contentAlignment = Alignment.Center
						) {
							Text(
								text = stringResource(Res.string.no_transactions),
								style = MaterialTheme.typography.bodyLarge,
								color = MaterialTheme.colorScheme.onSurfaceVariant
							)
						}
					}
				} else {
					items(transactions.reversed()) { transaction ->
						TransactionItem(transaction, players)
					}
				}
				item {
					Spacer(modifier = Modifier.height(32.dp))
				}
			}
		}
	}
}
