package com.duyts.pokerhost.presentation.ui.components.profile

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingDown
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.SessionPerformance
import com.duyts.pokerhost.util.CurrencyUtils
import com.duyts.pokerhost.util.DateTimeUtils

@Composable
fun RecentSessionItem(
	session: PokerSession,
	currentUserId: String?,
	onClick: () -> Unit,
) {
	val playerEntry = session.players.find { it.id == currentUserId }
	val profit = playerEntry?.netProfit ?: 0f
	val buyIn = playerEntry?.buyIn ?: 0f

	Card(
		modifier = Modifier
			.fillMaxWidth()
			.clickable { onClick() },
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
		),
		shape = MaterialTheme.shapes.medium,
		border = BorderStroke(
			0.5.dp,
			MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
		)
	) {
		Row(
			modifier = Modifier.padding(12.dp),
			verticalAlignment = Alignment.CenterVertically
		) {
			Surface(
				modifier = Modifier.size(48.dp),
				shape = MaterialTheme.shapes.medium,
				color = MaterialTheme.colorScheme.surfaceVariant
			) {
				Icon(
					imageVector = if (profit >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
					contentDescription = null,
					modifier = Modifier.padding(12.dp),
					tint = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
				)
			}
			Spacer(Modifier.width(16.dp))
			Column(modifier = Modifier.weight(1f)) {
				Text(
					text = session.title,
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold
				)
				Text(
					text = "${DateTimeUtils.formatDate(session.completedAt ?: session.createdAt)} • Buy-in: ${
						CurrencyUtils.format(
							buyIn
						)
					}",
					style = MaterialTheme.typography.bodySmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
			Column(horizontalAlignment = Alignment.End) {
				Text(
					text = (if (profit >= 0) "+" else "") + CurrencyUtils.format(profit),
					style = MaterialTheme.typography.titleMedium,
					fontWeight = FontWeight.Bold,
					color = if (profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
				)
				Text(
					text = if (profit > 0) {
						"ROI ${((profit / buyIn.coerceAtLeast(1f)) * 100).toInt()}%"
					} else if (profit < 0) {
						"BUSTED"
					} else {
						"BREAK EVEN"
					},
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}
		}
	}
}

@Composable
fun PerformanceItem(performance: SessionPerformance) {
	Card(
		modifier = Modifier.fillMaxWidth(),
		colors = CardDefaults.cardColors(
			containerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.5f)
		),
		shape = MaterialTheme.shapes.large,
		elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
		border = BorderStroke(
			0.5.dp,
			MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f)
		)
	) {
		Column(modifier = Modifier.padding(16.dp)) {
			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween,
				verticalAlignment = Alignment.CenterVertically
			) {
				Row(
					verticalAlignment = Alignment.CenterVertically,
					horizontalArrangement = Arrangement.spacedBy(12.dp)
				) {
					Surface(
						modifier = Modifier.size(40.dp),
						shape = CircleShape,
						color = if (performance.profit >= 0) MaterialTheme.colorScheme.primary.copy(
							alpha = 0.1f
						) else MaterialTheme.colorScheme.error.copy(alpha = 0.1f)
					) {
						Icon(
							imageVector = if (performance.profit >= 0) Icons.AutoMirrored.Filled.TrendingUp else Icons.AutoMirrored.Filled.TrendingDown,
							contentDescription = null,
							modifier = Modifier.padding(8.dp),
							tint = if (performance.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
						)
					}
					Column {
						Text(
							performance.sessionTitle,
							style = MaterialTheme.typography.titleMedium,
							fontWeight = FontWeight.Bold
						)
						Text(
							DateTimeUtils.formatDate(performance.completedAt),
							style = MaterialTheme.typography.bodySmall,
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
				Text(
					text = (if (performance.profit >= 0) "+" else "") + CurrencyUtils.format(
						performance.profit
					),
					style = MaterialTheme.typography.titleLarge,
					fontWeight = FontWeight.Black,
					color = if (performance.profit >= 0) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
				)
				Text(
					text = if (performance.profit > 0) {
						"ROI ${((performance.profit / performance.buyIn.coerceAtLeast(1f)) * 100).toInt()}%"
					} else if (performance.profit < 0) {
						"BUSTED"
					} else {
						"BREAK EVEN"
					},
					style = MaterialTheme.typography.labelSmall,
					color = MaterialTheme.colorScheme.onSurfaceVariant
				)
			}

			HorizontalDivider(
				modifier = Modifier.padding(vertical = 12.dp),
				thickness = 0.5.dp,
				color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.3f)
			)

			Row(
				modifier = Modifier.fillMaxWidth(),
				horizontalArrangement = Arrangement.SpaceBetween
			) {
				PerformanceDetailItem("Buy In", CurrencyUtils.format(performance.buyIn))
				PerformanceDetailItem("Cash Out", CurrencyUtils.format(performance.cashOut))
				PerformanceDetailItem("Adjust", CurrencyUtils.format(performance.adjustment))
			}
		}
	}
}

@Composable
private fun PerformanceDetailItem(label: String, value: String) {
	Column {
		Text(
			text = label,
			style = MaterialTheme.typography.labelSmall,
			color = MaterialTheme.colorScheme.onSurfaceVariant,
			fontSize = 10.sp
		)
		Text(
			text = value,
			style = MaterialTheme.typography.bodyMedium,
			fontWeight = FontWeight.Bold
		)
	}
}

