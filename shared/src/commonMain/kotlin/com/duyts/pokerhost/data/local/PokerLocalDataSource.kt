package com.duyts.pokerhost.data.local

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.data.PokerDataSource
import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.Transaction
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.DateTimeUtils
import com.duyts.pokerhost.util.IdGenerator
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.datetime.Clock
import me.tatarka.inject.annotations.Inject

interface PokerLocalDataSource : PokerDataSource

@Inject
class PokerLocalDataSourceImpl(
	private val database: InMemoryDatabase,
) : PokerLocalDataSource {

	override fun getSessions(userId: String): Flow<List<PokerSession>> =
		database.sessions.asStateFlow()

	override fun getSessionById(sessionId: String): Flow<PokerSession?> {
		return database.sessions.asStateFlow().map { sessions ->
			sessions.find { it.id == sessionId }
		}
	}

	override suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String> {
		val id = IdGenerator.generate("ses")
		return try {
			database.sessions.update { list ->
				val finalTitle = title?.takeIf { it.isNotBlank() }
					?: "Session ${DateTimeUtils.formatCurrentTimeHHmm()}"
				list + PokerSession(
					id = id,
					title = finalTitle,
					smallBlind = smallBlind,
					bigBlind = bigBlind,
					ownerId = userId,
					participantIds = listOf(userId),
					status = SessionStatus.ACTIVE,
					createdAt = Clock.System.now().toEpochMilliseconds()
				)
			}
			Result.Success(id)
		} catch (e: Exception) {
			Result.Error(message = e.message, exception = e)
		}
	}

	override suspend fun addPlayer(sessionId: String, id: String, name: String) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					val newPlayer = Player(id = id, name = name)
					val newParticipantIds = if (!session.participantIds.contains(id)) {
						session.participantIds + id
					} else {
						session.participantIds
					}
					session.copy(
						players = session.players + newPlayer,
						participantIds = newParticipantIds
					)
				} else session
			}
		}
	}

	override suspend fun updatePlayerName(sessionId: String, playerId: String, name: String) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						players = session.players.map { player ->
							if (player.id == playerId) player.copy(name = name) else player
						}
					)
				} else session
			}
		}
	}

	override suspend fun updatePlayerArchiveStatus(
		sessionId: String,
		playerId: String,
		isArchived: Boolean,
	) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						players = session.players.map { player ->
							if (player.id == playerId) player.copy(isArchived = isArchived) else player
						}
					)
				} else session
			}
		}
	}

	override suspend fun buyIn(sessionId: String, playerId: String, amount: Float) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						players = session.players.map { player ->
							if (player.id == playerId) player.copy(buyIn = player.buyIn + amount) else player
						}
					)
				} else session
			}
		}
	}

	override suspend fun cashOut(sessionId: String, playerId: String, amount: Float) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						players = session.players.map { player ->
							if (player.id == playerId) player.copy(cashOut = player.cashOut + amount) else player
						}
					)
				} else session
			}
		}
	}

	override suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						players = session.players.map { player ->
							when (player.id) {
								fromPlayerId -> player.copy(adjustment = player.adjustment - amount)
								toPlayerId -> player.copy(adjustment = player.adjustment + amount)
								else -> player
							}
						}
					)
				} else session
			}
		}
	}

	override suspend fun updateSessionTitle(sessionId: String, title: String) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(title = title)
				} else session
			}
		}
	}

	override suspend fun updateSessionStatus(sessionId: String, status: SessionStatus) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					session.copy(
						status = status,
						completedAt = if (status == SessionStatus.COMPLETED) Clock.System.now()
							.toEpochMilliseconds() else session.completedAt
					)
				} else session
			}
		}
	}

	override suspend fun saveTransaction(
		sessionId: String,
		type: TransactionType,
		amount: Float,
		playerId: String,
		targetPlayerId: String?,
	) {
		database.sessions.update { sessions ->
			sessions.map { session ->
				if (session.id == sessionId) {
					val newTransaction = Transaction(
						id = IdGenerator.generate("trx"),
						type = type,
						amount = amount,
						playerId = playerId,
						targetPlayerId = targetPlayerId,
						timestamp = Clock.System.now().toEpochMilliseconds()
					)
					session.copy(transactions = session.transactions + newTransaction)
				} else session
			}
		}
	}

	override suspend fun deleteSession(sessionId: String) {
		database.sessions.update { sessions ->
			sessions.filterNot { it.id == sessionId }
		}
	}
}
