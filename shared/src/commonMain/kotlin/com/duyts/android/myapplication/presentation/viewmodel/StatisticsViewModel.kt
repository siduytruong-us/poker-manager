package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.repository.SessionPerformance
import com.duyts.android.myapplication.domain.usecase.GetPerformanceHistoryUseCase
import kotlinx.coroutines.flow.*
import me.tatarka.inject.annotations.Inject

@Inject
class StatisticsViewModel(
    getPerformanceHistoryUseCase: GetPerformanceHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<StatisticsUiState> = getPerformanceHistoryUseCase()
        .map { history ->
            StatisticsUiState(
                performanceHistory = history,
                totalProfit = history.sumOf { it.profit.toDouble() }.toFloat(),
                totalBuyIn = history.sumOf { it.buyIn.toDouble() }.toFloat(),
                totalCashOut = history.sumOf { it.cashOut.toDouble() }.toFloat(),
                sessionsPlayed = history.size
            )
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = StatisticsUiState()
        )
}

data class StatisticsUiState(
    val performanceHistory: List<SessionPerformance> = emptyList(),
    val totalProfit: Float = 0f,
    val totalBuyIn: Float = 0f,
    val totalCashOut: Float = 0f,
    val sessionsPlayed: Int = 0
)
