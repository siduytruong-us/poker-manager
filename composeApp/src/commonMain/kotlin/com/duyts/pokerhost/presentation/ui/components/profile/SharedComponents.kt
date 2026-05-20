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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.PreviewParameter
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.SessionPerformance
import com.duyts.pokerhost.fake.FakeData
import com.duyts.pokerhost.presentation.theme.AppTheme
import com.duyts.pokerhost.presentation.theme.ThemePreviewProvider
import com.duyts.pokerhost.util.CurrencyUtils
import com.duyts.pokerhost.util.DateTimeUtils
import org.jetbrains.compose.resources.stringResource
import pokerhost.composeapp.generated.resources.Res
import pokerhost.composeapp.generated.resources.adjust
import pokerhost.composeapp.generated.resources.break_even
import pokerhost.composeapp.generated.resources.busted
import pokerhost.composeapp.generated.resources.buy_in
import pokerhost.composeapp.generated.resources.buy_in_bullet_format
import pokerhost.composeapp.generated.resources.cash_out
import pokerhost.composeapp.generated.resources.roi_format

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
					text = "${DateTimeUtils.formatDate(session.completedAt ?: session.createdAt)}${
						stringResource(
							Res.string.buy_in_bullet_format,
							CurrencyUtils.format(buyIn)
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
						stringResource(
							Res.string.roi_format,
							((profit / buyIn.coerceAtLeast(1f)) * 100).toInt()
						)
					} else if (profit < 0) {
						stringResource(Res.string.busted)
					} else {
						stringResource(Res.string.break_even)
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
			containerColor = MaterialTheme.colorScheme.surface
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
						stringResource(
							Res.string.roi_format,
							((performance.profit / performance.buyIn.coerceAtLeast(1f)) * 100).toInt()
						)
					} else if (performance.profit < 0) {
						stringResource(Res.string.busted)
					} else {
						stringResource(Res.string.break_even)
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
				PerformanceDetailItem(
					stringResource(Res.string.buy_in),
					CurrencyUtils.format(performance.buyIn)
				)
				PerformanceDetailItem(
					stringResource(Res.string.cash_out),
					CurrencyUtils.format(performance.cashOut)
				)
				PerformanceDetailItem(
					stringResource(Res.string.adjust),
					CurrencyUtils.format(performance.adjustment)
				)
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

@Preview
@Composable
fun RecentSessionItemPreview(
	@PreviewParameter(ThemePreviewProvider::class) darkTheme: Boolean,
) {
	AppTheme(darkTheme = darkTheme) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			RecentSessionItem(
				session = FakeData.session,
				currentUserId = FakeData.playerId,
				onClick = {}
			)

			RecentSessionItem(
				session = FakeData.session.copy(
					players = listOf(
						FakeData.player.copy(cashOut = 50f)
					)
				),
				currentUserId = FakeData.playerId,
				onClick = {}
			)
		}
	}
}

@Preview
@Composable
fun PerformanceItemPreview(
	@PreviewParameter(ThemePreviewProvider::class) darkTheme: Boolean,
) {
	AppTheme(darkTheme = darkTheme) {
		Column(
			modifier = Modifier.padding(16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			PerformanceItem(performance = FakeData.performance)

			PerformanceItem(
				performance = FakeData.performance.copy(
					profit = -150f,
					cashOut = 350f,
					sessionTitle = "Rough Session"
				)
			)
		}
	}
}
