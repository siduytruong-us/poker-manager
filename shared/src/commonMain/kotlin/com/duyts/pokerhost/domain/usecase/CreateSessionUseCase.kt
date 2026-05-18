package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.PokerRepository
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

@Inject
class CreateSessionUseCase(
	private val repository: PokerRepository,
	private val authRepository: AuthRepository,
) {
	suspend operator fun invoke(
		title: String?,
		smallBlind: Float,
		bigBlind: Float,
	): Result<String> = try {
		val user = authRepository.currentUser.first() ?: throw Exception("User not logged in")
		val result = repository.createSession(user.id, title, smallBlind, bigBlind)
		if (result is Result.Success) {
			val sessionId = result.data
			user.displayName?.let { name ->
				repository.addPlayer(sessionId, user.id, name)
			}
		}
		result
	} catch (e: Exception) {
		Result.Error(message = e.message, exception = e)
	}
}
