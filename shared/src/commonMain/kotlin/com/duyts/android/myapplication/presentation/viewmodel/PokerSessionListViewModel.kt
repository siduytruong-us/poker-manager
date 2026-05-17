package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.core.Result
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.usecase.CompleteSessionUseCase
import com.duyts.android.myapplication.domain.usecase.CreateSessionUseCase
import com.duyts.android.myapplication.domain.usecase.DeleteSessionUseCase
import com.duyts.android.myapplication.domain.usecase.GetSessionsUseCase
import com.duyts.android.myapplication.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class PokerSessionListViewModel(
    getSessionsUseCase: GetSessionsUseCase,
    private val createSessionUseCase: CreateSessionUseCase,
    private val deleteSessionUseCase: DeleteSessionUseCase,
    private val completeSessionUseCase: CompleteSessionUseCase,
    authRepository: AuthRepository,
) : ViewModel() {

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    val uiState: StateFlow<PokerSessionListUiState> = combine(
        getSessionsUseCase(),
        authRepository.currentUser
    ) { sessions, user ->
        val groupedSessions = sessions
            .sortedByDescending { it.createdAt }
            .groupBy { DateTimeUtils.formatDate(it.createdAt) }

        PokerSessionListUiState.Success(
            groupedSessions = groupedSessions,
            currentUserId = user?.id,
            userPhotoUrl = user?.photoUrl
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5000),
        initialValue = PokerSessionListUiState.Loading
    )

    fun createSession(title: String?, smallBlind: Float, bigBlind: Float) {
        viewModelScope.launch {
            when (val result = createSessionUseCase(title, smallBlind, bigBlind)) {
                is Result.Success -> {
                    _error.value = null
                }
                is Result.Error -> {
                    _error.value = result.message ?: result.exception?.message ?: "Failed to create session"
                }
            }
        }
    }

    fun deleteSession(id: String) {
        viewModelScope.launch {
            deleteSessionUseCase(id)
        }
    }

    fun completeSession(id: String) {
        viewModelScope.launch {
            completeSessionUseCase(id)
        }
    }
}

sealed class PokerSessionListUiState {
    object Loading : PokerSessionListUiState()
    data class Success(
        val groupedSessions: Map<String, List<PokerSession>>,
        val currentUserId: String? = null,
        val userPhotoUrl: String? = null,
    ) : PokerSessionListUiState()
    data class Error(val message: String) : PokerSessionListUiState()
}
