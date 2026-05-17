package com.duyts.android.myapplication.presentation.ui.statistics

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.EventNote
import androidx.compose.material.icons.filled.EmojiEvents
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.duyts.android.myapplication.domain.repository.SessionPerformance
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.ui.components.profile.PerformanceItem
import com.duyts.android.myapplication.presentation.ui.statistics.components.StatCard
import com.duyts.android.myapplication.presentation.viewmodel.StatisticsUiState
import com.duyts.android.myapplication.util.CurrencyUtils
import com.duyts.android.myapplication.util.DateTimeUtils
import com.himanshoe.charty.color.ChartyColor
import com.himanshoe.charty.common.config.ChartScaffoldConfig
import com.himanshoe.charty.line.LineChart
import com.himanshoe.charty.line.config.LineChartConfig
import com.himanshoe.charty.line.data.LineData
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.net_profit
import myapplication.composeapp.generated.resources.no_completed_sessions
import myapplication.composeapp.generated.resources.profit_over_time
import myapplication.composeapp.generated.resources.session_history
import myapplication.composeapp.generated.resources.sessions
import myapplication.composeapp.generated.resources.statistics
import myapplication.composeapp.generated.resources.total_buy_in
import myapplication.composeapp.generated.resources.total_cash_out
import org.jetbrains.compose.resources.stringResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StatisticsScreen(
	state: StatisticsUiState,
	scrollToHistory: Boolean = false,
) {
	val listState = rememberLazyListState()

	LaunchedEffect(scrollToHistory, state.performanceHistory) {
		if (scrollToHistory && state.performanceHistory.isNotEmpty()) {
			listState.animateScrollToItem(5) // Adjusted index for new sections
		}
	}

	Scaffold(
		topBar = {
			CenterAlignedTopAppBar(
				title = {
					Text(
						stringResource(Res.string.statistics),
						fontWeight = FontWeight.Bold
					)
				},
				colors = TopAppBarDefaults.topAppBarColors(
					containerColor = MaterialTheme.colorScheme.background
				)
			)
		}
	) { padding ->
		LazyColumn(
			state = listState,
			modifier = Modifier
				.fillMaxSize()
				.padding(padding)
				.padding(horizontal = 16.dp),
			verticalArrangement = Arrangement.spacedBy(16.dp)
		) {
			item {
				Spacer(modifier = Modifier.height(8.dp))
				// Hero Profit Section
				Box(
					modifier = Modifier
						.fillMaxWidth()
						.height(100.dp)
						.background(
							color = MaterialTheme.colorScheme.surfaceVariant.copy(
								alpha = 0.5f
							),
							shape = MaterialTheme.shapes.extraLarge
						)
						.padding(24.dp),
					contentAlignment = Alignment.Center
				) {
					Column(horizontalAlignment = Alignment.CenterHorizontally) {
						Text(
							text = stringResource(Res.string.net_profit).uppercase(),
							style = MaterialTheme.typography.labelMedium,
							color = MaterialTheme.colorScheme.onPrimaryContainer,
							letterSpacing = 2.sp
						)
						Text(
							text = CurrencyUtils.format(state.totalProfit),
							style = MaterialTheme.typography.headlineLarge,
							fontWeight = FontWeight.Black,
							color = if (state.totalProfit >= 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error,
							fontSize = 36.sp
						)
					}
				}
			}

			item {
				// Summary Stats Grid
				Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					) {
						StatCard(
							label = stringResource(Res.string.total_buy_in),
							value = CurrencyUtils.format(state.totalBuyIn),
							icon = Icons.Default.Payments,
							iconColor = MaterialTheme.colorScheme.primary,
							modifier = Modifier.weight(1f)
						)
						StatCard(
							label = stringResource(Res.string.total_cash_out),
							value = CurrencyUtils.format(state.totalCashOut),
							icon = Icons.Default.PriceCheck,
							iconColor = MaterialTheme.colorScheme.primary, // Using primary for cashout too (positive)
							modifier = Modifier.weight(1f)
						)
					}
					Row(
						modifier = Modifier.fillMaxWidth(),
						horizontalArrangement = Arrangement.spacedBy(12.dp)
					) {
						StatCard(
							label = stringResource(Res.string.sessions),
							value = state.sessionsPlayed.toString(),
							icon = Icons.AutoMirrored.Filled.EventNote,
							iconColor = MaterialTheme.colorScheme.tertiary,
							modifier = Modifier.weight(1f)
						)
						StatCard(
							label = "Win Rate",
							value = if (state.sessionsPlayed > 0) {
								val wins = state.performanceHistory.count { it.profit > 0 }
								"${(wins.toFloat() / state.sessionsPlayed * 100).toInt()}%"
							} else "0%",
							icon = Icons.Default.EmojiEvents,
							iconColor = Color(0xFFFFD700), // Gold
							modifier = Modifier.weight(1f)
						)
					}
				}
			}

			if (state.performanceHistory.isNotEmpty()) {
				item {
					Text(
						text = stringResource(Res.string.profit_over_time),
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold
					)
					Spacer(modifier = Modifier.height(8.dp))
					ProfitChart(
						history = state.performanceHistory.reversed(),
						modifier = Modifier
							.fillMaxWidth()
							.height(250.dp)
							.background(
								color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f),
								shape = MaterialTheme.shapes.large
							)
							.padding(16.dp)
					)
				}

				item {
					Text(
						text = stringResource(Res.string.session_history),
						style = MaterialTheme.typography.titleLarge,
						fontWeight = FontWeight.Bold,
						modifier = Modifier.padding(top = 8.dp)
					)
				}

				items(state.performanceHistory) { performance ->
					PerformanceItem(performance)
				}
			} else {
				item {
					Box(
						modifier = Modifier.fillMaxWidth().padding(vertical = 64.dp),
						contentAlignment = Alignment.Center
					) {
						Text(
							text = stringResource(Res.string.no_completed_sessions),
							color = MaterialTheme.colorScheme.onSurfaceVariant
						)
					}
				}
			}

			item {
				Spacer(modifier = Modifier.height(16.dp))
			}
		}
	}
}

@Composable
fun ProfitChart(
    history: List<SessionPerformance>,
	modifier: Modifier = Modifier,
) {
	if (history.isEmpty()) return

	var currentCumulative = 0f
	val lineData = history.map { performance ->
		currentCumulative += performance.profit
		LineData(
			label = DateTimeUtils.formatDate(performance.completedAt).substringBefore(" ").take(5),
			value = currentCumulative
		)
	}

	LineChart(
		modifier = modifier,
		data = { lineData },
		scaffoldConfig = ChartScaffoldConfig(
			axisColor = MaterialTheme.colorScheme.onSurfaceVariant,
			labelTextStyle = MaterialTheme.typography.labelSmall.copy(
				color = MaterialTheme.colorScheme.onSurfaceVariant
			),
		),
		color = ChartyColor.Solid(MaterialTheme.colorScheme.primary),
		lineConfig = LineChartConfig(
			lineWidth = 4f,
			showPoints = true,
			pointRadius = 5f,
			smoothCurve = true
		)
	)
}

@Preview(showSystemUi = true)
@Composable
fun StatisticsScreenPreview() {
	val mockHistory = listOf(
		SessionPerformance(
			sessionId = "1",
			sessionTitle = "Friday Night",
			completedAt = 1715000000000L,
			profit = 50f,
			buyIn = 100f,
			cashOut = 150f,
			adjustment = 0f
		),
		SessionPerformance(
			sessionId = "2",
			sessionTitle = "Saturday Cash",
			completedAt = 1715100000000L,
			profit = -20f,
			buyIn = 100f,
			cashOut = 80f,
			adjustment = 0f
		),
		SessionPerformance(
			sessionId = "3",
			sessionTitle = "Home Game",
			completedAt = 1715200000000L,
			profit = 100f,
			buyIn = 200f,
			cashOut = 300f,
			adjustment = 0f
		)
	)
	AppTheme(darkTheme = true) {
		StatisticsScreen(
			state = StatisticsUiState(
				performanceHistory = mockHistory,
				totalProfit = 130f,
				totalBuyIn = 400f,
				totalCashOut = 530f,
				sessionsPlayed = 3
			)
		)
	}
}
