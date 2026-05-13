package com.duyts.android.myapplication.domain.repository

import com.duyts.android.myapplication.domain.model.PokerSession
import kotlinx.coroutines.flow.Flow

interface PokerRepository {
    fun getSessions(): Flow<List<PokerSession>>
    fun getSessionById(sessionId: String): Flow<PokerSession?>
    suspend fun createSession(title: String?, smallBlind: Float, bigBlind: Float)
    suspend fun addPlayer(sessionId: String, name: String)
    suspend fun buyIn(sessionId: String, playerId: String, amount: Float)
    suspend fun cashOut(sessionId: String, playerId: String, amount: Float)
    suspend fun transferBetweenPlayers(sessionId: String, fromPlayerId: String, toPlayerId: String, amount: Float)
    suspend fun updateSessionTitle(sessionId: String, title: String)
    suspend fun deleteSession(sessionId: String)
}
