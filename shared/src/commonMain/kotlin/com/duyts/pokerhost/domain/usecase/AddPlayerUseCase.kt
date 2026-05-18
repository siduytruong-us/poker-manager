package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.repository.PokerRepository
import com.duyts.pokerhost.util.IdGenerator
import me.tatarka.inject.annotations.Inject

@Inject
class AddPlayerUseCase(private val repository: PokerRepository) {
	suspend operator fun invoke(sessionId: String, name: String, id: String? = null) {
		val finalId = id ?: IdGenerator.generate("ply")
		repository.addPlayer(sessionId, finalId, name)
	}
}
