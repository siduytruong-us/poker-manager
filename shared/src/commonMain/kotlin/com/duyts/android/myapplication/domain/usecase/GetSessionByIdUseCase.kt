package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.repository.PokerRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class GetSessionByIdUseCase(private val repository: PokerRepository) {
    operator fun invoke(sessionId: String): Flow<PokerSession?> = repository.getSessionById(sessionId)
}
