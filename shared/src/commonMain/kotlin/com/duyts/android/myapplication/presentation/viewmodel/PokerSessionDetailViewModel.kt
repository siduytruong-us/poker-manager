package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.usecase.AddPlayerUseCase
import com.duyts.android.myapplication.domain.usecase.BuyInUseCase
import com.duyts.android.myapplication.domain.usecase.CashOutUseCase
import com.duyts.android.myapplication.domain.usecase.GetSessionByIdUseCase
import com.duyts.android.myapplication.domain.usecase.TransferBetweenPlayersUseCase
import com.duyts.android.myapplication.domain.usecase.UpdateSessionTitleUseCase
import kotlinx.coroutines.flow.*
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
) : ViewModel() {

	val uiState: StateFlow<PokerSessionDetailUiState> = getSessionByIdUseCase(sessionId)
		.map { session ->
			if (session == null) {
				PokerSessionDetailUiState.Error("Session not found")
			} else {
				PokerSessionDetailUiState.Success(session)
			}
		}
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = PokerSessionDetailUiState.Loading
		)

	fun addPlayer(name: String) {
		viewModelScope.launch {
			addPlayerUseCase(sessionId, name)
		}
	}

	fun buyIn(playerId: String, amount: Float) {
		viewModelScope.launch {
			buyInUseCase(sessionId, playerId, amount)
		}
	}

	fun cashOut(playerId: String, amount: Float) {
		viewModelScope.launch {
			cashOutUseCase(sessionId, playerId, amount)
		}
	}

	fun transfer(fromPlayerId: String, toPlayerId: String, amount: Float) {
		viewModelScope.launch {
			transferBetweenPlayersUseCase(sessionId, fromPlayerId, toPlayerId, amount)
		}
	}

	fun updateTitle(title: String) {
		viewModelScope.launch {
			updateSessionTitleUseCase(sessionId, title)
		}
	}
}

sealed class PokerSessionDetailUiState {
	object Loading : PokerSessionDetailUiState()
	data class Success(
		val session: PokerSession,
	) : PokerSessionDetailUiState() {

		val playerAmountSuggestion: List<Float>
			get() = calculateSuggestions()

		private fun calculateSuggestions(): List<Float> =
			listOf(1f, 2f, 3f, 5f, 10f, 20f, 50f, 100f, 200f, 500f)

	}

	data class Error(val message: String) : PokerSessionDetailUiState()
}
