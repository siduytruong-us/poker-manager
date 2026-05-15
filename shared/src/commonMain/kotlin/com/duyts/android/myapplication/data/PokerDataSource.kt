package com.duyts.android.myapplication.data

import com.duyts.android.myapplication.core.Result
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.TransactionType
import kotlinx.coroutines.flow.Flow

interface PokerDataSource {
    fun getSessions(userId: String): Flow<List<PokerSession>>
    fun getSessionById(sessionId: String): Flow<PokerSession?>
    suspend fun createSession(userId: String, title: String?, smallBlind: Float, bigBlind: Float): Result<String>
    suspend fun addPlayer(sessionId: String, id: String, name: String)
    suspend fun buyIn(sessionId: String, playerId: String, amount: Float)
    suspend fun cashOut(sessionId: String, playerId: String, amount: Float)
    suspend fun transferBetweenPlayers(sessionId: String, fromPlayerId: String, toPlayerId: String, amount: Float)
    suspend fun updateSessionTitle(sessionId: String, title: String)
    suspend fun saveTransaction(sessionId: String, type: TransactionType, amount: Float, playerId: String, targetPlayerId: String? = null)
    suspend fun deleteSession(sessionId: String)
}
