package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.core.Result
import com.duyts.android.myapplication.domain.model.PokerSession
import com.duyts.android.myapplication.domain.usecase.GetSessionByIdUseCase
import com.duyts.android.myapplication.domain.usecase.JoinSessionUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class MainViewModel(
    private val getSessionByIdUseCase: GetSessionByIdUseCase,
    private val joinSessionUseCase: JoinSessionUseCase
) : ViewModel() {

	private val _sessionToJoin = MutableStateFlow<PokerSession?>(null)
	val sessionToJoin: StateFlow<PokerSession?> = _sessionToJoin.asStateFlow()

	fun handleDeepLink(sessionId: String?) {
		if (sessionId.isNullOrBlank()) return
		viewModelScope.launch {
			_sessionToJoin.value = getSessionByIdUseCase(sessionId).firstOrNull()
		}
	}

	fun onDismissJoinDialog() {
		_sessionToJoin.value = null
	}

    fun joinSession(sessionId: String, onResult: (Result<Unit>) -> Unit) {
        viewModelScope.launch {
            val result = joinSessionUseCase(sessionId)
            onResult(result)
        }
    }
}
