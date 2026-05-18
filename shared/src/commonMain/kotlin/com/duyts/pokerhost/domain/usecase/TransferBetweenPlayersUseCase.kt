package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class TransferBetweenPlayersUseCase(private val repository: PokerRepository) {
	suspend operator fun invoke(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	) =
		repository.transferBetweenPlayers(sessionId, fromPlayerId, toPlayerId, amount)
}
