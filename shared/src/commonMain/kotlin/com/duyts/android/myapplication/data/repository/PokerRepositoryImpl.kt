package com.duyts.android.myapplication.data.repository

import com.duyts.android.myapplication.data.PokerDataSource
import com.duyts.android.myapplication.data.local.PokerLocalDataSource
import com.duyts.android.myapplication.data.remote.PokerRemoteDataSource
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.model.TransactionType
import com.duyts.android.myapplication.domain.repository.PokerRepository
import com.duyts.android.myapplication.di.AppScope
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
@AppScope
class PokerRepositoryImpl(
    private val localDataSource: PokerLocalDataSource,
    private val remoteDataSource: PokerRemoteDataSource? = null
) : PokerRepository {

    private val dataSource: PokerDataSource get() = remoteDataSource ?: localDataSource

    override fun getSessions(): Flow<List<PokerSession>> = dataSource.getSessions()

    override fun getSessionById(sessionId: String): Flow<PokerSession?> = 
        dataSource.getSessionById(sessionId)

    override suspend fun createSession(title: String?, smallBlind: Float, bigBlind: Float) {
        dataSource.createSession(title, smallBlind, bigBlind)
    }

    override suspend fun addPlayer(sessionId: String, name: String) {
        dataSource.addPlayer(sessionId, name)
    }

    override suspend fun buyIn(sessionId: String, playerId: String, amount: Float) {
        dataSource.buyIn(sessionId, playerId, amount)
        dataSource.saveTransaction(sessionId, TransactionType.BUY_IN, amount, playerId)
    }

    override suspend fun cashOut(sessionId: String, playerId: String, amount: Float) {
        dataSource.cashOut(sessionId, playerId, amount)
        dataSource.saveTransaction(sessionId, TransactionType.CASH_OUT, amount, playerId)
    }

    override suspend fun transferBetweenPlayers(sessionId: String, fromPlayerId: String, toPlayerId: String, amount: Float) {
        dataSource.transferBetweenPlayers(sessionId, fromPlayerId, toPlayerId, amount)
        dataSource.saveTransaction(sessionId, TransactionType.TRANSFER, amount, fromPlayerId, toPlayerId)
    }

    override suspend fun updateSessionTitle(sessionId: String, title: String) {
        dataSource.updateSessionTitle(sessionId, title)
    }

    override suspend fun deleteSession(sessionId: String) {
        dataSource.deleteSession(sessionId)
    }
}
