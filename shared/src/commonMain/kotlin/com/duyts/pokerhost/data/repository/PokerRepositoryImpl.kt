package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.data.PokerDataSource
import com.duyts.pokerhost.data.local.PokerLocalDataSource
import com.duyts.pokerhost.data.remote.PokerRemoteDataSource
import com.duyts.pokerhost.di.AppScope
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.domain.repository.PokerRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
@AppScope
class PokerRepositoryImpl(
	private val localDataSource: PokerLocalDataSource,
	private val remoteDataSource: PokerRemoteDataSource? = null,
) : PokerRepository {

	private val dataSource: PokerDataSource get() = remoteDataSource ?: localDataSource

	override fun getSessions(userId: String): Flow<List<PokerSession>> =
		dataSource.getSessions(userId)

	override fun getSessionById(sessionId: String): Flow<PokerSession?> =
		dataSource.getSessionById(sessionId)

	override suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String> =
		dataSource.createSession(userId, title, smallBlind, bigBlind)

	override suspend fun addPlayer(sessionId: String, id: String, name: String) {
		dataSource.addPlayer(sessionId, id, name)
	}

	override suspend fun updatePlayerName(sessionId: String, playerId: String, name: String) {
		dataSource.updatePlayerName(sessionId, playerId, name)
	}

	override suspend fun updatePlayerArchiveStatus(
		sessionId: String,
		playerId: String,
		isArchived: Boolean,
	) {
		dataSource.updatePlayerArchiveStatus(sessionId, playerId, isArchived)
	}

	override suspend fun buyIn(sessionId: String, playerId: String, amount: Float) {
		dataSource.buyIn(sessionId, playerId, amount)
		dataSource.saveTransaction(sessionId, TransactionType.BUY_IN, amount, playerId)
	}

	override suspend fun cashOut(sessionId: String, playerId: String, amount: Float) {
		dataSource.cashOut(sessionId, playerId, amount)
		dataSource.saveTransaction(sessionId, TransactionType.CASH_OUT, amount, playerId)
	}

	override suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	) {
		dataSource.transferBetweenPlayers(sessionId, fromPlayerId, toPlayerId, amount)
		dataSource.saveTransaction(
			sessionId,
			TransactionType.TRANSFER,
			amount,
			fromPlayerId,
			toPlayerId
		)
	}

	override suspend fun updateSessionTitle(sessionId: String, title: String) {
		dataSource.updateSessionTitle(sessionId, title)
	}

	override suspend fun updateSessionStatus(sessionId: String, status: SessionStatus) {
		dataSource.updateSessionStatus(sessionId, status)
	}

	override suspend fun deleteSession(sessionId: String) {
		dataSource.deleteSession(sessionId)
	}
}
