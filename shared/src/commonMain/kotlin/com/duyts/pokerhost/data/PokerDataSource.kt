package com.duyts.pokerhost.data

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface PokerDataSource {
	fun getSessions(userId: String): Flow<List<PokerSession>>
	fun getSessionById(sessionId: String): Flow<PokerSession?>
	suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String>

	suspend fun addPlayer(sessionId: String, id: String, name: String)
	suspend fun updatePlayerName(sessionId: String, playerId: String, name: String)
	suspend fun updatePlayerArchiveStatus(sessionId: String, playerId: String, isArchived: Boolean)
	suspend fun buyIn(sessionId: String, playerId: String, amount: Float)
	suspend fun cashOut(sessionId: String, playerId: String, amount: Float)
	suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	)

	suspend fun updateSessionTitle(sessionId: String, title: String)
	suspend fun updateSessionStatus(
		sessionId: String,
		status: com.duyts.pokerhost.domain.model.SessionStatus,
	)

	suspend fun saveTransaction(
		sessionId: String,
		type: TransactionType,
		amount: Float,
		playerId: String,
		targetPlayerId: String? = null,
	)

	suspend fun deleteSession(sessionId: String)
}
