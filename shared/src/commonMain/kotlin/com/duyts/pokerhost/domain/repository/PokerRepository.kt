package com.duyts.pokerhost.domain.repository

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.model.PokerSession
import kotlinx.coroutines.flow.Flow

interface PokerRepository {
	fun getSessions(userId: String): Flow<List<PokerSession>>
	fun getSessionById(sessionId: String): Flow<PokerSession?>
	suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String>

	suspend fun addPlayer(sessionId: String, id: String, name: String): Result<Unit>
	suspend fun updatePlayerName(sessionId: String, playerId: String, name: String): Result<Unit>
	suspend fun updatePlayerArchiveStatus(
		sessionId: String,
		playerId: String,
		isArchived: Boolean,
	): Result<Unit>

	suspend fun buyIn(sessionId: String, playerId: String, amount: Float): Result<Unit>
	suspend fun cashOut(sessionId: String, playerId: String, amount: Float): Result<Unit>
	suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	): Result<Unit>

	suspend fun updateSessionTitle(sessionId: String, title: String): Result<Unit>
	suspend fun updateSessionStatus(
		sessionId: String,
		status: com.duyts.pokerhost.domain.model.SessionStatus,
	): Result<Unit>

	suspend fun deleteSession(sessionId: String): Result<Unit>
}
