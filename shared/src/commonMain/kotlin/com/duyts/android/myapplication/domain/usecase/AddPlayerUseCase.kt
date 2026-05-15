package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.repository.PokerRepository
import com.duyts.android.myapplication.util.IdGenerator
import me.tatarka.inject.annotations.Inject

@Inject
class AddPlayerUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, name: String, id: String? = null) {
        val finalId = id ?: IdGenerator.generate("ply")
        repository.addPlayer(sessionId, finalId, name)
    }
}
