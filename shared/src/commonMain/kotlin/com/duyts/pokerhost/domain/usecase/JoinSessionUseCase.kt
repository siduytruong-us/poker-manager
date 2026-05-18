package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.core.Result
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.PokerRepository
import kotlinx.coroutines.flow.first
import me.tatarka.inject.annotations.Inject

@Inject
class JoinSessionUseCase(
	private val repository: PokerRepository,
	private val authRepository: AuthRepository,
) {
	suspend operator fun invoke(sessionId: String): Result<Unit> = try {
		val user = authRepository.currentUser.first() ?: throw Exception("User not logged in")
		val session =
			repository.getSessionById(sessionId).first() ?: throw Exception("Session not found")

		val isAlreadyJoined = session.players.any { it.id == user.id }

		if (!isAlreadyJoined) {
			val name = user.displayName ?: "User ${user.id.takeLast(4)}"
			repository.addPlayer(sessionId, user.id, name)
		}

		Result.Success(Unit)
	} catch (e: Exception) {
		Result.Error(message = e.message, exception = e)
	}
}
