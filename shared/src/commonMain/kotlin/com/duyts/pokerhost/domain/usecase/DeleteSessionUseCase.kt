package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class DeleteSessionUseCase(private val repository: PokerRepository) {
	suspend operator fun invoke(sessionId: String) = repository.deleteSession(sessionId)
}
