package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.model.AppConfig
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.usecase.CompleteSessionUseCase
import com.duyts.pokerhost.domain.usecase.CreateSessionUseCase
import com.duyts.pokerhost.domain.usecase.DeleteSessionUseCase
import com.duyts.pokerhost.domain.usecase.GetAppConfigUseCase
import com.duyts.pokerhost.domain.usecase.GetSessionsUseCase
import com.duyts.pokerhost.util.DateTimeUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class DashboardViewModel(
	getSessionsUseCase: GetSessionsUseCase,
	getAppConfigUseCase: GetAppConfigUseCase,
	private val createSessionUseCase: CreateSessionUseCase,
	private val deleteSessionUseCase: DeleteSessionUseCase,
	private val completeSessionUseCase: CompleteSessionUseCase,
	authRepository: AuthRepository,
) : ViewModel() {

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error

	val uiState: StateFlow<PokerSessionListUiState> = combine(
		getSessionsUseCase(),
		getAppConfigUseCase(),
		authRepository.currentUser
	) { sessions, config, user ->
		val groupedSessions = sessions
			.sortedByDescending { it.createdAt }
			.groupBy { DateTimeUtils.formatDate(it.createdAt) }

		PokerSessionListUiState.Success(
			groupedSessions = groupedSessions,
			currentUserId = user?.id,
			userPhotoUrl = user?.photoUrl,
			appConfig = config
		)
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = PokerSessionListUiState.Loading
	)

	private val _sessionCreated = MutableStateFlow<String?>(null)
	val sessionCreated: StateFlow<String?> = _sessionCreated

	fun createSession(title: String?, smallBlind: Float, bigBlind: Float) {
		viewModelScope.launch {
			when (val result = createSessionUseCase(title, smallBlind, bigBlind)) {
				is Result.Success -> {
					_error.value = null
					_sessionCreated.value = result.data
				}

				is Result.Error -> {
					_error.value =
						result.message ?: result.exception?.message ?: "Failed to create session"
				}
			}
		}
	}

	fun resetSessionCreated() {
		_sessionCreated.value = null
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
		val appConfig: AppConfig? = null,
	) : PokerSessionListUiState()

	data class Error(val message: String) : PokerSessionListUiState()
}
