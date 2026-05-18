package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.PokerRepository
import kotlinx.coroutines.flow.Flow
import me.tatarka.inject.annotations.Inject

@Inject
class GetSessionByIdUseCase(private val repository: PokerRepository) {
	operator fun invoke(sessionId: String): Flow<PokerSession?> =
		repository.getSessionById(sessionId)
}
