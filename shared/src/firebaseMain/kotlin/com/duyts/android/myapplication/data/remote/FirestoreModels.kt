package com.duyts.android.myapplication.data.remote

import com.duyts.android.myapplication.domain.model.Player
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.Transaction
import com.duyts.android.myapplication.domain.model.TransactionType
import com.duyts.android.myapplication.util.CurrencyUtils
import kotlinx.serialization.Serializable

@Serializable
data class FirestoreSession(
    val id: String = "", // Format: ses.uuid
    val title: String = "",
    val smallBlind: Int = 0,
    val bigBlind: Int = 0,
    val ownerId: String = "",
    val createdAt: Long = 0L
)

@Serializable
data class FirestorePlayer(
    val id: String = "", // Format: ply.uuid
    val name: String = "",
    val buyIn: Int = 0,
    val cashOut: Int = 0,
    val adjustment: Int = 0
)

@Serializable
data class FirestoreTransaction(
    val id: String = "", // Format: trx.uuid
    val type: String = "",
    val amount: Int = 0,
    val playerId: String = "",
    val targetPlayerId: String? = null,
    val timestamp: Long = 0L
)

fun FirestoreSession.toDomain(players: List<FirestorePlayer>, transactions: List<Transaction>) = PokerSession(
    id = id,
    title = title,
    smallBlind = CurrencyUtils.centsToDollars(smallBlind),
    bigBlind = CurrencyUtils.centsToDollars(bigBlind),
    players = players.map { it.toDomain() },
    transactions = transactions,
    createdAt = createdAt
)

fun FirestorePlayer.toDomain() = Player(
    id = id,
    name = name,
    buyIn = CurrencyUtils.centsToDollars(buyIn),
    cashOut = CurrencyUtils.centsToDollars(cashOut),
    adjustment = CurrencyUtils.centsToDollars(adjustment)
)

fun FirestoreTransaction.toDomain() = Transaction(
    id = id,
    type = TransactionType.valueOf(type),
    amount = CurrencyUtils.centsToDollars(amount),
    playerId = playerId,
    targetPlayerId = targetPlayerId,
    timestamp = timestamp
)
