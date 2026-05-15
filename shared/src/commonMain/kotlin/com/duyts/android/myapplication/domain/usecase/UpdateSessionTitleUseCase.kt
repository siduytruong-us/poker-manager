package com.duyts.android.myapplication.domain.usecase

import com.duyts.android.myapplication.domain.repository.PokerRepository
import me.tatarka.inject.annotations.Inject

@Inject
class UpdateSessionTitleUseCase(private val repository: PokerRepository) {
    suspend operator fun invoke(sessionId: String, title: String) =
        repository.updateSessionTitle(sessionId, title)
}
