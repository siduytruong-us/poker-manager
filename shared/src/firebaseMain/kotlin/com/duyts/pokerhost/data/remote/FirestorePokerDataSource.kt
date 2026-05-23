package com.duyts.pokerhost.data.remote

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.CurrencyUtils
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import kotlin.time.Clock

@Inject
@OptIn(ExperimentalCoroutinesApi::class)
class FirestorePokerDataSource(
	private val firestoreService: SessionsFirestoreService,
) : PokerRemoteDataSource {

	override fun getSessions(userId: String): Flow<List<PokerSession>> {
		return firestoreService.getSessionsQuery(userId)
			.snapshots()
			.flatMapLatest { snapshot ->
				if (snapshot.documents.isEmpty()) return@flatMapLatest flowOf(emptyList())

				val sessionFlows = snapshot.documents.map { doc ->
					val fsSession = doc.data<FirestoreSession>().copy(id = doc.id)
					val playersFlow =
						doc.reference.collection("players").snapshots().map { playerSnapshot ->
							playerSnapshot.documents.map { it.data<FirestorePlayer>() }
						}
					val transactionsFlow =
						doc.reference.collection("transactions").snapshots().map { txSnapshot ->
							txSnapshot.documents.map { it.data<FirestoreTransaction>().toDomain() }
						}

					combine(playersFlow, transactionsFlow) { players, transactions ->
						fsSession.toDomain(players, transactions)
					}
				}
				combine(sessionFlows) { it.toList() }
			}
	}

	override fun getSessionById(sessionId: String): Flow<PokerSession?> {
		return firestoreService.getSessionDocument(sessionId)
			.snapshots()
			.flatMapLatest { docSnapshot ->
				if (!docSnapshot.exists) return@flatMapLatest flowOf(null)
				val fsSession = docSnapshot.data<FirestoreSession>().copy(id = docSnapshot.id)

				val playersFlow =
					docSnapshot.reference.collection("players").snapshots().map { playerSnapshot ->
						playerSnapshot.documents.map { it.data<FirestorePlayer>() }
					}
				val transactionsFlow =
					docSnapshot.reference.collection("transactions").snapshots().map { txSnapshot ->
						txSnapshot.documents.map { it.data<FirestoreTransaction>().toDomain() }
					}

				combine(playersFlow, transactionsFlow) { players, transactions ->
					fsSession.toDomain(players, transactions)
				}
			}
	}

	override suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String> {
		return try {
			val id = firestoreService.createSession(userId, title, smallBlind, bigBlind)
			Result.Success(id)
		} catch (e: Exception) {
			Result.Error(message = e.message, exception = e)
		}
	}

	override suspend fun addPlayer(sessionId: String, id: String, name: String) {
		firestoreService.addPlayer(sessionId, id, name)
	}

	override suspend fun updatePlayerName(sessionId: String, playerId: String, name: String) {
		firestoreService.updatePlayerName(sessionId, playerId, name)
	}

	override suspend fun updatePlayerArchiveStatus(
		sessionId: String,
		playerId: String,
		isArchived: Boolean,
	) {
		firestoreService.updatePlayerArchiveStatus(sessionId, playerId, isArchived)
	}

	override suspend fun buyIn(sessionId: String, playerId: String, amount: Float) {
		firestoreService.updatePlayerBalance(
			sessionId = sessionId,
			playerId = playerId,
			buyInDelta = CurrencyUtils.dollarsToCents(amount)
		)
	}

	override suspend fun cashOut(sessionId: String, playerId: String, amount: Float) {
		firestoreService.updatePlayerBalance(
			sessionId = sessionId,
			playerId = playerId,
			cashOutDelta = CurrencyUtils.dollarsToCents(amount)
		)
	}

	override suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	) {
		val centAmount = CurrencyUtils.dollarsToCents(amount)

		firestoreService.updatePlayerBalance(
			sessionId = sessionId,
			playerId = fromPlayerId,
			adjustmentDelta = -centAmount
		)
		firestoreService.updatePlayerBalance(
			sessionId = sessionId,
			playerId = toPlayerId,
			adjustmentDelta = centAmount
		)
	}

	override suspend fun updateSessionTitle(sessionId: String, title: String) {
		firestoreService.updateSession(sessionId, mapOf("title" to title))
	}

	override suspend fun updateSessionStatus(sessionId: String, status: SessionStatus) {
		val updates = mutableMapOf<String, Any>(
			"status" to status.toFirestore()
		)
		if (status == SessionStatus.COMPLETED) {
			updates["completedAt"] = Clock.System.now().toEpochMilliseconds()
		}
		firestoreService.updateSession(sessionId, updates)
	}

	override suspend fun saveTransaction(
		sessionId: String,
		type: TransactionType,
		amount: Float,
		playerId: String,
		targetPlayerId: String?,
	) {
		firestoreService.saveTransaction(sessionId, type, amount, playerId, targetPlayerId)
	}

	override suspend fun deleteSession(sessionId: String) {
		firestoreService.deleteSession(sessionId)
	}
}
