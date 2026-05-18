package com.duyts.pokerhost.data.remote

import com.duyts.pokerhost.domain.model.Player
import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.model.Transaction
import com.duyts.pokerhost.domain.model.TransactionType
import com.duyts.pokerhost.util.CurrencyUtils
import kotlinx.serialization.Serializable

@Serializable
data class FirestoreUser(
	val id: String = "",
	val email: String? = null,
	val displayName: String? = null,
	val photoUrl: String? = null,
	val createdAt: Long = 0L,
)

@Serializable
enum class FirestoreSessionStatus {
	ACTIVE, COMPLETED
}

fun FirestoreSessionStatus.toDomain(): SessionStatus = when (this) {
	FirestoreSessionStatus.ACTIVE -> SessionStatus.ACTIVE
	FirestoreSessionStatus.COMPLETED -> SessionStatus.COMPLETED
}

fun SessionStatus.toFirestore(): FirestoreSessionStatus = when (this) {
	SessionStatus.ACTIVE -> FirestoreSessionStatus.ACTIVE
	SessionStatus.COMPLETED -> FirestoreSessionStatus.COMPLETED
}

@Serializable
data class FirestoreSession(
	val id: String = "", // Format: ses.uuid
	val title: String = "",
	val smallBlind: Int = 0,
	val bigBlind: Int = 0,
	val ownerId: String = "",
	val participantIds: List<String> = emptyList(),
	val status: FirestoreSessionStatus = FirestoreSessionStatus.ACTIVE,
	val createdAt: Long = 0L,
	val completedAt: Long? = null,
)

@Serializable
data class FirestorePlayer(
	val id: String = "",
	val name: String = "",
	val buyIn: Int = 0,
	val cashOut: Int = 0,
	val adjustment: Int = 0,
	val isArchived: Boolean = false,
)

@Serializable
data class FirestoreTransaction(
	val id: String = "", // Format: trx.uuid
	val type: String = "",
	val amount: Int = 0,
	val playerId: String = "",
	val targetPlayerId: String? = null,
	val timestamp: Long = 0L,
)

fun FirestoreSession.toDomain(players: List<FirestorePlayer>, transactions: List<Transaction>) =
	PokerSession(
		id = id,
		title = title,
		smallBlind = CurrencyUtils.centsToDollars(smallBlind),
		bigBlind = CurrencyUtils.centsToDollars(bigBlind),
		ownerId = ownerId,
		players = players.map { it.toDomain() },
		participantIds = participantIds,
		transactions = transactions,
		status = status.toDomain(),
		createdAt = createdAt,
		completedAt = completedAt
	)

fun FirestorePlayer.toDomain() = Player(
	id = id,
	name = name,
	buyIn = CurrencyUtils.centsToDollars(buyIn),
	cashOut = CurrencyUtils.centsToDollars(cashOut),
	adjustment = CurrencyUtils.centsToDollars(adjustment),
	isArchived = isArchived
)

fun FirestoreTransaction.toDomain() = Transaction(
	id = id,
	type = TransactionType.valueOf(type),
	amount = CurrencyUtils.centsToDollars(amount),
	playerId = playerId,
	targetPlayerId = targetPlayerId,
	timestamp = timestamp
)
