package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class CompleteSessionUseCase(private val repository: PokerRepository) {
	suspend operator fun invoke(sessionId: String) =
		repository.updateSessionStatus(sessionId, SessionStatus.COMPLETED)
}
