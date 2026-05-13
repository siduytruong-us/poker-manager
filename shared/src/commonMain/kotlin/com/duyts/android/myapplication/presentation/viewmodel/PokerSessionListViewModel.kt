package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.usecase.CreateSessionUseCase
import com.duyts.android.myapplication.domain.usecase.DeleteSessionUseCase
import com.duyts.android.myapplication.domain.usecase.GetSessionsUseCase
import com.duyts.android.myapplication.util.DateTimeUtils
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class PokerSessionListViewModel(
    getSessionsUseCase: GetSessionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase
) : ViewModel() {

    val uiState: StateFlow<PokerSessionListUiState> = getSessionsUseCase()
        .map { sessions -> 
            val groupedSessions = sessions
                .sortedByDescending { it.createdAt }
                .groupBy { DateTimeUtils.formatDate(it.createdAt) }
            
            PokerSessionListUiState.Success(groupedSessions) 
        }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = PokerSessionListUiState.Loading
        )

    fun createSession(title: String?, smallBlind: Float, bigBlind: Float) {
        viewModelScope.launch {
            createSessionUseCase(title, smallBlind, bigBlind)
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            deleteSessionUseCase(id)
        }
    }
}

sealed class PokerSessionListUiState {
    object Loading : PokerSessionListUiState()
    data class Success(val groupedSessions: Map<String, List<PokerSession>>) : PokerSessionListUiState()
    data class Error(val message: String) : PokerSessionListUiState()
}
