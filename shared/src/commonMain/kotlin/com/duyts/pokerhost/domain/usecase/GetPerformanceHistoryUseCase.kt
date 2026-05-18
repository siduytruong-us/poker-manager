package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.model.SessionStatus
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.PokerRepository
import com.duyts.pokerhost.domain.repository.SessionPerformance
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class GetPerformanceHistoryUseCase(
	private val authRepository: AuthRepository,
	private val pokerRepository: PokerRepository,
) {
	@OptIn(ExperimentalCoroutinesApi::class)
	operator fun invoke(): Flow<List<SessionPerformance>> {
		return authRepository.currentUser.flatMapLatest { user ->
			if (user == null) return@flatMapLatest flowOf(emptyList())

			pokerRepository.getSessions(user.id).map { sessions ->
				val completedSessions = sessions.filter { it.status == SessionStatus.COMPLETED }
				val performanceHistory = mutableListOf<SessionPerformance>()

				completedSessions.forEach { session ->
					val playerEntry = session.players.find { it.id == user.id }
					if (playerEntry != null) {
						performanceHistory.add(
							SessionPerformance(
								sessionId = session.id,
								sessionTitle = session.title,
								completedAt = session.completedAt ?: session.createdAt,
								profit = playerEntry.netProfit,
								buyIn = playerEntry.buyIn,
								cashOut = playerEntry.cashOut,
								adjustment = playerEntry.adjustment
							)
						)
					}
				}

				performanceHistory.sortedByDescending { it.completedAt }
			}
		}
	}
}
