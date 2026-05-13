package com.duyts.android.myapplication.data.local

import com.duyts.android.myapplication.di.AppScope
import com.duyts.android.myapplication.domain.model.PokerSession
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject

@AppScope
class InMemoryDatabase @Inject constructor() {
    val sessions = MutableStateFlow<List<PokerSession>>(emptyList())

    fun clear() {
        sessions.value = emptyList()
    }
}
