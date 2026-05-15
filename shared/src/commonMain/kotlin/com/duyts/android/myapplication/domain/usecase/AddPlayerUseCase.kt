package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class AddPlayerUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, name: String) = repository.addPlayer(sessionId, name)
}
