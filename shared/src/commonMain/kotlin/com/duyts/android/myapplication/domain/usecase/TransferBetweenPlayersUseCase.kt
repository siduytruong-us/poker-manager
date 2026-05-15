package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class TransferBetweenPlayersUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, fromPlayerId: String, toPlayerId: String, amount: Float) =
        repository.transferBetweenPlayers(sessionId, fromPlayerId, toPlayerId, amount)
}
