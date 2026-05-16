package com.duyts.android.myapplication.data.remote

import com.duyts.android.myapplication.core.Result
import com.duyts.android.myapplication.data.local.PokerLocalDataSource
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.SessionStatus
import com.duyts.android.myapplication.domain.model.TransactionType
import com.duyts.android.myapplication.util.CurrencyUtils
import com.duyts.android.myapplication.util.DateTimeUtils
import com.duyts.android.myapplication.util.IdGenerator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import dev.gitlive.firebase.firestore.where
import kotlinx.datetime.Clock
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
@OptIn(ExperimentalCoroutinesApi::class)
class FirestorePokerDataSource : PokerRemoteDataSource {
	private val firestore = Firebase.firestore

	private val sessionsCollection = firestore.collection("sessions")

	override fun getSessions(userId: String): Flow<List<PokerSession>> {
		return sessionsCollection.where { "participantIds" contains userId }
			.snapshots()
			.flatMapLatest { snapshot ->
				if (snapshot.documents.isEmpty()) return@flatMapLatest flowOf(emptyList())

				val sessionFlows = snapshot.documents.map { doc ->
					val fsSession = doc.data<FirestoreSession>().copy(id = doc.id)
					val playersFlow = doc.reference.collection("players").snapshots().map { playerSnapshot ->
						playerSnapshot.documents.map { it.data<FirestorePlayer>() }
					}
					val transactionsFlow = doc.reference.collection("transactions").snapshots().map { txSnapshot ->
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
		return sessionsCollection.document(sessionId)
			.snapshots()
			.flatMapLatest { docSnapshot ->
				if (!docSnapshot.exists) return@flatMapLatest flowOf(null)
				val fsSession = docSnapshot.data<FirestoreSession>().copy(id = docSnapshot.id)

				val playersFlow = docSnapshot.reference.collection("players").snapshots().map { playerSnapshot ->
					playerSnapshot.documents.map { it.data<FirestorePlayer>() }
				}
				val transactionsFlow = docSnapshot.reference.collection("transactions").snapshots().map { txSnapshot ->
					txSnapshot.documents.map { it.data<FirestoreTransaction>().toDomain() }
				}

				combine(playersFlow, transactionsFlow) { players, transactions ->
					fsSession.toDomain(players, transactions)
				}
			}
	}

	override suspend fun createSession(userId: String, title: String?, smallBlind: Float, bigBlind: Float): Result<String> {
		val docId = IdGenerator.generate("ses")
		val docRef = sessionsCollection.document(docId)
		val finalTitle = title?.takeIf { it.isNotBlank() } ?: "Session ${DateTimeUtils.formatCurrentTimeHHmm()}"
		val session = FirestoreSession(
			id = docRef.id,
			title = finalTitle,
			smallBlind = CurrencyUtils.dollarsToCents(smallBlind),
			bigBlind = CurrencyUtils.dollarsToCents(bigBlind),
			ownerId = userId,
			participantIds = listOf(userId),
			createdAt = Clock.System.now().toEpochMilliseconds()
		)
		return try {
			docRef.set(session)
			Result.Success(docRef.id)
		} catch (e: Exception) {
			Result.Error(message = e.message, exception = e)
		}
	}

	override suspend fun addPlayer(sessionId: String, id: String, name: String) {
		val playerRef = sessionsCollection.document(sessionId).collection("players")
			.document(id)
		val player = FirestorePlayer(
			id = id,
			name = name
		)
		playerRef.set(player)

		// If the ID doesn't look like a generated ply. prefix, it's a Firebase UID
		if (!id.startsWith("ply.")) {
			sessionsCollection.document(sessionId).update(
				"participantIds" to FieldValue.arrayUnion(id)
			)
		}
	}

	override suspend fun updatePlayerName(sessionId: String, playerId: String, name: String) {
		sessionsCollection.document(sessionId).collection("players").document(playerId)
			.update("name" to name)
	}

	override suspend fun updatePlayerArchiveStatus(sessionId: String, playerId: String, isArchived: Boolean) {
		sessionsCollection.document(sessionId).collection("players").document(playerId)
			.update("isArchived" to isArchived)
	}

	override suspend fun buyIn(sessionId: String, playerId: String, amount: Float) {
		val playerRef =
			sessionsCollection.document(sessionId).collection("players").document(playerId)
		val currentBuyIn = playerRef.get().data<FirestorePlayer>().buyIn
		playerRef.update("buyIn" to currentBuyIn + CurrencyUtils.dollarsToCents(amount))
	}

	override suspend fun cashOut(sessionId: String, playerId: String, amount: Float) {
		val playerRef =
			sessionsCollection.document(sessionId).collection("players").document(playerId)
		val currentCashOut = playerRef.get().data<FirestorePlayer>().cashOut
		playerRef.update("cashOut" to currentCashOut + CurrencyUtils.dollarsToCents(amount))
	}

	override suspend fun transferBetweenPlayers(
		sessionId: String,
		fromPlayerId: String,
		toPlayerId: String,
		amount: Float,
	) {
		val playersCol = sessionsCollection.document(sessionId).collection("players")

		val fromPlayerRef = playersCol.document(fromPlayerId)
		val toPlayerRef = playersCol.document(toPlayerId)

		val fromAdj = fromPlayerRef.get().data<FirestorePlayer>().adjustment
		val toAdj = toPlayerRef.get().data<FirestorePlayer>().adjustment
		val centAmount = CurrencyUtils.dollarsToCents(amount)

		fromPlayerRef.update("adjustment" to fromAdj - centAmount)
		toPlayerRef.update("adjustment" to toAdj + centAmount)
	}

	override suspend fun updateSessionTitle(sessionId: String, title: String) {
		sessionsCollection.document(sessionId).update("title" to title)
	}

	override suspend fun updateSessionStatus(sessionId: String, status: SessionStatus) {
		val updates = mutableMapOf<String, Any>(
			"status" to status.toFirestore()
		)
		if (status == SessionStatus.COMPLETED) {
			updates["completedAt"] = Clock.System.now().toEpochMilliseconds()
		}
		sessionsCollection.document(sessionId).update(updates)
	}

	override suspend fun saveTransaction(
		sessionId: String,
		type: TransactionType,
		amount: Float,
		playerId: String,
		targetPlayerId: String?,
	) {
		val txRef = sessionsCollection.document(sessionId).collection("transactions")
			.document(IdGenerator.generate("trx"))
		val transaction = FirestoreTransaction(
			id = txRef.id,
			type = type.name,
			amount = CurrencyUtils.dollarsToCents(amount),
			playerId = playerId,
			targetPlayerId = targetPlayerId,
			timestamp = Clock.System.now().toEpochMilliseconds()
		)
		txRef.set(transaction)
	}

	override suspend fun deleteSession(sessionId: String) {
		sessionsCollection.document(sessionId).delete()
	}
}
