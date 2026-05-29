package com.duyts.pokerhost.data.remote

import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.ClockUtils
import com.duyts.pokerhost.util.CurrencyUtils
import com.duyts.pokerhost.util.DateTimeUtils
import com.duyts.pokerhost.util.IdGenerator
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.FieldValue
import dev.gitlive.firebase.firestore.firestore
import me.tatarka.inject.annotations.Inject

@Inject
class SessionsFirestoreService {
	private val firestore = Firebase.firestore
	private val sessionsCollection = firestore.collection("sessions")

	fun getSessionsQuery(userId: String) =
		sessionsCollection.where { "participantIds" contains userId }

	fun getSessionDocument(sessionId: String) = sessionsCollection.document(sessionId)

	suspend fun createSession(
		userId: String,
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): String {
		val docId = IdGenerator.generate("ses")
		val docRef = sessionsCollection.document(docId)
		val finalTitle =
			title?.takeIf { it.isNotBlank() } ?: "Session ${DateTimeUtils.formatCurrentTimeHHmm()}"
		val session = FirestoreSession(
			id = docRef.id,
			title = finalTitle,
			smallBlind = CurrencyUtils.dollarsToCents(smallBlind),
			bigBlind = CurrencyUtils.dollarsToCents(bigBlind),
			ownerId = userId,
			participantIds = listOf(userId),
			createdAt = ClockUtils.now().toEpochMilliseconds()
		)
		docRef.set(FirestoreSession.serializer(), session)
		return docRef.id
	}

	suspend fun addPlayer(sessionId: String, id: String, name: String) {
		val sessionRef = sessionsCollection.document(sessionId)
		val playerRef = sessionRef.collection("players").document(id)
		val player = FirestorePlayer(
			id = id,
			name = name
		)

		firestore.batch().apply {
			set(playerRef, FirestorePlayer.serializer(), player)
			if (!id.startsWith("ply.")) {
				updateFields(sessionRef) { "participantIds" to FieldValue.arrayUnion(id) }
			}
		}.commit()
	}

	suspend fun updatePlayerName(sessionId: String, playerId: String, name: String) {
		sessionsCollection.document(sessionId).collection("players").document(playerId)
			.updateFields { "name" to name }
	}

	suspend fun updatePlayerArchiveStatus(
		sessionId: String,
		playerId: String,
		isArchived: Boolean,
	) {
		sessionsCollection.document(sessionId).collection("players").document(playerId)
			.updateFields { "isArchived" to isArchived }
	}

	suspend fun getPlayer(sessionId: String, playerId: String) =
		sessionsCollection.document(sessionId).collection("players").document(playerId).get()

	suspend fun updatePlayer(sessionId: String, playerId: String, updates: Map<String, Any?>) {
		val playerRef =
			sessionsCollection.document(sessionId).collection("players").document(playerId)
		if (updates.isNotEmpty()) {
			playerRef.update(updates)
		}
	}

	suspend fun updatePlayerBalance(
        sessionId: String,
        playerId: String,
        buyInDelta: Int = 0,
        cashOutDelta: Int = 0,
        adjustmentDelta: Int = 0,
    ) {
		val playerRef =
			sessionsCollection.document(sessionId).collection("players").document(playerId)
		firestore.runTransaction {
			val snapshot = get(playerRef)
			val player = snapshot.data<FirestorePlayer>()

			val updates = mutableMapOf<String, Any?>()
			if (buyInDelta != 0) updates["buyIn"] = player.buyIn + buyInDelta
			if (cashOutDelta != 0) updates["cashOut"] = player.cashOut + cashOutDelta
			if (adjustmentDelta != 0) updates["adjustment"] = player.adjustment + adjustmentDelta

			if (updates.isNotEmpty()) {
				update(playerRef, updates)
			}
		}
	}

	suspend fun updateSession(sessionId: String, updates: Map<String, Any?>) {
		val sessionRef = sessionsCollection.document(sessionId)
		if (updates.isNotEmpty()) {
			sessionRef.update(updates)
		}
	}

	suspend fun saveTransaction(
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
			timestamp = ClockUtils.now().toEpochMilliseconds()
		)
		txRef.set(FirestoreTransaction.serializer(), transaction)
	}

	suspend fun deleteSession(sessionId: String) {
		sessionsCollection.document(sessionId).delete()
	}
}
