package com.duyts.android.myapplication.presentation.ui.statistics

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.domain.repository.SessionPerformance
import com.duyts.android.myapplication.presentation.theme.AppTheme
import com.duyts.android.myapplication.presentation.ui.profile.PerformanceItem
import com.duyts.android.myapplication.presentation.ui.profile.ProfileStatItem
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
    state: StatisticsUiState
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(stringResource(Res.string.statistics)) }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Spacer(modifier = Modifier.height(8.dp))
                // Summary Stats
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly
                        ) {
                            ProfileStatItem(
								stringResource(Res.string.total_buy_in), CurrencyUtils.format(state.totalBuyIn)
							)
                            ProfileStatItem(
                                label = stringResource(Res.string.total_cash_out),
                                value = CurrencyUtils.format(state.totalCashOut)
                            )
                        }
                        HorizontalDivider(color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.1f))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceEvenly,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ProfileStatItem(
                                label = stringResource(Res.string.sessions),
                                value = state.sessionsPlayed.toString()
                            )
                            ProfileStatItem(
                                label = stringResource(Res.string.net_profit),
                                value = CurrencyUtils.format(state.totalProfit),
                                valueColor = if (state.totalProfit >= 0f) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.error
                            )
                        }
                    }
                }
            }

            if (state.performanceHistory.isNotEmpty()) {
                item {
                    Text(
                        text = stringResource(Res.string.profit_over_time),
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfitChart(
                        history = state.performanceHistory.reversed(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(300.dp)
                            .padding(16.dp)
                    )
                }

                item {
                    Text(
                        text = stringResource(Res.string.session_history),
                        style = MaterialTheme.typography.titleLarge,
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
    modifier: Modifier = Modifier
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
            lineWidth = 3f,
            showPoints = true,
            pointRadius = 4f,
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
    AppTheme {
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
