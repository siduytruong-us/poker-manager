package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.AuthUser
import com.duyts.android.myapplication.domain.repository.SessionPerformance
import com.duyts.android.myapplication.domain.usecase.GetPerformanceHistoryUseCase
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileViewModel(
    private val authRepository: AuthRepository,
    getPerformanceHistoryUseCase: GetPerformanceHistoryUseCase
) : ViewModel() {

    val uiState: StateFlow<ProfileUiState> = combine(
        authRepository.currentUser,
        getPerformanceHistoryUseCase()
    ) { user, history ->
        ProfileUiState(
            user = user,
            performanceHistory = history,
            totalProfit = history.sumOf { it.profit.toDouble() }.toFloat(),
            sessionsPlayed = history.size
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = ProfileUiState()
    )

    fun logout() {
        viewModelScope.launch {
            authRepository.signOut()
        }
    }
}

data class ProfileUiState(
    val user: AuthUser? = null,
    val performanceHistory: List<SessionPerformance> = emptyList(),
    val totalProfit: Float = 0f,
    val sessionsPlayed: Int = 0
)
