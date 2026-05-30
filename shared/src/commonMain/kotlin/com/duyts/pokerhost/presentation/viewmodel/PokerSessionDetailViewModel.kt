package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.usecase.AddPlayerUseCase
import com.duyts.pokerhost.domain.usecase.BuyInUseCase
import com.duyts.pokerhost.domain.usecase.CashOutUseCase
import com.duyts.pokerhost.domain.usecase.CompleteSessionUseCase
import com.duyts.pokerhost.domain.usecase.GetSessionByIdUseCase
import com.duyts.pokerhost.domain.usecase.TransferBetweenPlayersUseCase
import com.duyts.pokerhost.domain.usecase.UpdatePlayerArchiveStatusUseCase
import com.duyts.pokerhost.domain.usecase.UpdatePlayerNameUseCase
import com.duyts.pokerhost.domain.usecase.UpdateSessionTitleUseCase
import com.duyts.pokerhost.util.ShareManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Assisted
import me.tatarka.inject.annotations.Inject

@Inject
class PokerSessionDetailViewModel(
	@Assisted private val sessionId: String,
	getSessionByIdUseCase: GetSessionByIdUseCase,
	private val addPlayerUseCase: AddPlayerUseCase,
	private val buyInUseCase: BuyInUseCase,
	private val cashOutUseCase: CashOutUseCase,
	private val transferBetweenPlayersUseCase: TransferBetweenPlayersUseCase,
	private val updateSessionTitleUseCase: UpdateSessionTitleUseCase,
	private val updatePlayerNameUseCase: UpdatePlayerNameUseCase,
	private val updatePlayerArchiveStatusUseCase: UpdatePlayerArchiveStatusUseCase,
	private val completeSessionUseCase: CompleteSessionUseCase,
	private val shareManager: ShareManager,
	authRepository: AuthRepository,
) : ViewModel() {

	private val _error = MutableStateFlow<String?>(null)
	val error: StateFlow<String?> = _error.asStateFlow()

	val uiState: StateFlow<PokerSessionDetailUiState> = combine(
		getSessionByIdUseCase(sessionId),
		authRepository.currentUser
	) { session, user ->
		if (session == null) {
			PokerSessionDetailUiState.Error("Session not found")
		} else {
			PokerSessionDetailUiState.Success(
				session = session,
				isOwner = session.ownerId == user?.id
			)
		}
	}.stateIn(
		scope = viewModelScope,
		started = SharingStarted.WhileSubscribed(5000),
		initialValue = PokerSessionDetailUiState.Loading
	)

	fun addPlayer(name: String) {
		viewModelScope.launch {
			addPlayerUseCase(sessionId, name).onError { _error.value = it }
		}
	}

	fun buyIn(playerId: String, amount: Float) {
		viewModelScope.launch {
			buyInUseCase(sessionId, playerId, amount).onError { _error.value = it }
		}
	}

	fun cashOut(playerId: String, amount: Float) {
		viewModelScope.launch {
			cashOutUseCase(sessionId, playerId, amount).onError { _error.value = it }
		}
	}

	fun transfer(fromPlayerId: String, toPlayerId: String, amount: Float) {
		viewModelScope.launch {
			transferBetweenPlayersUseCase(sessionId, fromPlayerId, toPlayerId, amount)
				.onError { _error.value = it }
		}
	}

	fun updateTitle(title: String) {
		viewModelScope.launch {
			updateSessionTitleUseCase(sessionId, title).onError { _error.value = it }
		}
	}

	fun updatePlayerName(playerId: String, name: String) {
		viewModelScope.launch {
			updatePlayerNameUseCase(sessionId, playerId, name).onError { _error.value = it }
		}
	}

	fun updatePlayerArchiveStatus(playerId: String, isArchived: Boolean) {
		viewModelScope.launch {
			updatePlayerArchiveStatusUseCase(sessionId, playerId, isArchived)
				.onError { _error.value = it }
		}
	}

	fun completeSession() {
		viewModelScope.launch {
			completeSessionUseCase(sessionId).onError { _error.value = it }
		}
	}

	fun clearError() {
		_error.value = null
	}

	private fun Result<Unit>.onError(block: (String) -> Unit) {
		if (this is Result.Error) block(message ?: exception?.message ?: "Unknown error")
	}

	fun shareSession(title: String) {
		shareManager.shareSession(sessionId, title)
	}
}

sealed class PokerSessionDetailUiState {
	object Loading : PokerSessionDetailUiState()
	data class Success(
		val session: PokerSession,
		val isOwner: Boolean = false,
	) : PokerSessionDetailUiState() {

		val playerAmountSuggestion: List<Float>
			get() = calculateSuggestions()

		private fun calculateSuggestions(): List<Float> =
			listOf(1f, 2f, 3f, 5f, 10f, 20f, 50f, 100f, 200f, 500f)

	}

	data class Error(val message: String) : PokerSessionDetailUiState()
}
