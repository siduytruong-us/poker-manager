package com.duyts.android.myapplication.presentation.ui.statistics

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.unit.dp
import com.duyts.android.myapplication.presentation.viewmodel.StatisticsUiState
import com.duyts.android.myapplication.presentation.ui.profile.PerformanceItem
import com.duyts.android.myapplication.presentation.ui.profile.ProfileStatItem
import com.duyts.android.myapplication.util.CurrencyUtils
import myapplication.composeapp.generated.resources.Res
import myapplication.composeapp.generated.resources.statistics
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
                                label = "Total Buy-in",
                                value = CurrencyUtils.format(state.totalBuyIn)
                            )
                            ProfileStatItem(
                                label = "Total Cash-out",
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
                                label = "Sessions",
                                value = state.sessionsPlayed.toString()
                            )
                            ProfileStatItem(
                                label = "Net Profit",
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
                        text = "Profit Over Time",
                        style = MaterialTheme.typography.titleLarge
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    ProfitChart(
                        history = state.performanceHistory.reversed(),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant, MaterialTheme.shapes.medium)
                            .padding(16.dp)
                    )
                }

                item {
                    Text(
                        text = "Session History",
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
                        Text("No completed sessions yet", color = MaterialTheme.colorScheme.onSurfaceVariant)
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
    history: List<com.duyts.android.myapplication.domain.repository.SessionPerformance>,
    modifier: Modifier = Modifier
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val errorColor = MaterialTheme.colorScheme.error
    val onSurface = MaterialTheme.colorScheme.onSurfaceVariant

    Canvas(modifier = modifier) {
        if (history.isEmpty()) return@Canvas

        val spacing = size.width / (history.size + 1)
        val maxProfit = history.maxOf { kotlin.math.abs(it.profit) }.coerceAtLeast(10f)
        val centerY = size.height / 2
        val scale = (size.height / 2.2f) / maxProfit // Keep some margin

        // Baseline
        drawLine(
            color = onSurface,
            start = Offset(0f, centerY),
            end = Offset(size.width, centerY),
            strokeWidth = 1.dp.toPx(),
            alpha = 0.3f
        )

        // Draw individual session profit bars
        history.forEachIndexed { index, performance ->
            val x = spacing * (index + 1)
            val barHeight = performance.profit * scale
            val color = if (performance.profit >= 0) primaryColor else errorColor
            
            drawLine(
                color = color,
                start = Offset(x, centerY),
                end = Offset(x, centerY - barHeight),
                strokeWidth = 12.dp.toPx()
            )
        }
    }
}
