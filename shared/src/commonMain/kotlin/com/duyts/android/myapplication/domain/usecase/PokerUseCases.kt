package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.repository.PokerRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class GetSessionsUseCase(private val repository: PokerRepository) {
    operator fun invoke(): Flow<List<PokerSession>> = repository.getSessions()
}

@Inject
class GetSessionByIdUseCase(private val repository: PokerRepository) {
    operator fun invoke(sessionId: String): Flow<PokerSession?> = repository.getSessionById(sessionId)
}

@Inject
class CreateSessionUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(title: String?, smallBlind: Float, bigBlind: Float) = 
        repository.createSession(title, smallBlind, bigBlind)
}

@Inject
class AddPlayerUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, name: String) = repository.addPlayer(sessionId, name)
}

@Inject
class BuyInUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, playerId: String, amount: Float) = 
        repository.buyIn(sessionId, playerId, amount)
}

@Inject
class CashOutUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, playerId: String, amount: Float) = 
        repository.cashOut(sessionId, playerId, amount)
}

@Inject
class TransferBetweenPlayersUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, fromPlayerId: String, toPlayerId: String, amount: Float) =
        repository.transferBetweenPlayers(sessionId, fromPlayerId, toPlayerId, amount)
}

@Inject
class UpdateSessionTitleUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, title: String) =
        repository.updateSessionTitle(sessionId, title)
}

@Inject
class DeleteSessionUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String) = repository.deleteSession(sessionId)
}
