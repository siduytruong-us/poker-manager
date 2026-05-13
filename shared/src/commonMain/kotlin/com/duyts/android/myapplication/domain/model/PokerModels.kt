package com.duyts.android.myapplication.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class TransactionType {
    BUY_IN, CASH_OUT, TRANSFER
}

@Serializable
data class Transaction(
    val id: String, // Format: trx.uuid
    val type: TransactionType,
    val amount: Float,
    val playerId: String, // trx.uuid
    val targetPlayerId: String? = null,
    val timestamp: Long = 0L
)

@Serializable
data class Player(
    val id: String, // Format: ply.uuid
    val name: String,
    val buyIn: Float = 0f,
    val cashOut: Float = 0f,
    val adjustment: Float = 0f
) {
    val netProfit: Float get() = cashOut - buyIn + adjustment
}

@Serializable
data class PokerSession(
    val id: String, // Format: ses.uuid
    val title: String,
    val smallBlind: Float = 0f,
    val bigBlind: Float = 0f,
    val players: List<Player> = emptyList(),
    val transactions: List<Transaction> = emptyList(),
    val isFinished: Boolean = false,
    val createdAt: Long = 0L
) {
    val totalBuyIn: Float get() = players.sumOf { it.buyIn.toDouble() }.toFloat()
    val totalCashOut: Float get() = players.sumOf { it.cashOut.toDouble() }.toFloat()
    val totalAdjustment: Float get() = players.sumOf { it.adjustment.toDouble() }.toFloat()
}
