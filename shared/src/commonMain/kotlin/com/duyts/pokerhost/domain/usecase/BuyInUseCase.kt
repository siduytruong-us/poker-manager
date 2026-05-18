package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class BuyInUseCase(private val repository: PokerRepository) {
	suspend operator fun invoke(sessionId: String, playerId: String, amount: Float) =
		repository.buyIn(sessionId, playerId, amount)
}
