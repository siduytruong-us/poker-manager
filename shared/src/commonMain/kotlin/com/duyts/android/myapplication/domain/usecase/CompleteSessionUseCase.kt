package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.model.SessionStatus
import com.duyts.android.myapplication.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class CompleteSessionUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String) =
        repository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)
}
