package com.duyts.pokerhost.data.local

import com.duyts.pokerhost.di.AppScope
import com.duyts.pokerhost.domain.model.PokerSession
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
class InMemoryDatabase @Inject constructor() {
	val sessions = MutableStateFlow<List<PokerSession>>(emptyList())

	fun clear() {
		sessions.value = emptyList()
	}
}
