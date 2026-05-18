package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class StartViewModel(
	private val authRepository: AuthRepository,
	private val settingsManager: SettingsManager,
) : ViewModel() {

	private val _startDestination = MutableStateFlow<StartDestination>(StartDestination.Checking)
	val startDestination: StateFlow<StartDestination> = _startDestination

	init {
		checkDestination()
	}

	private fun checkDestination() {
		viewModelScope.launch {
			if (settingsManager.isFirstTime) {
				_startDestination.value = StartDestination.Onboarding
			} else {
				val user = authRepository.currentUser.first()
				if (user != null) {
					_startDestination.value = StartDestination.Main
				} else {
					_startDestination.value = StartDestination.Login
				}
			}
		}
	}

	fun completeOnboarding() {
		settingsManager.isFirstTime = false
		checkDestination()
	}
}

sealed class StartDestination {
	object Checking : StartDestination()
	object Onboarding : StartDestination()
	object Login : StartDestination()
	object Main : StartDestination()
}
