package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.model.PokerSession
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.PokerRepository
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class GetSessionsUseCase(
	private val repository: PokerRepository,
	private val authRepository: AuthRepository,
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<PokerSession>> =
		authRepository.currentUser.flatMapLatest { user ->
			val uid = user?.id ?: return@flatMapLatest flowOf(emptyList())
			repository.getSessions(uid)
		}
}
