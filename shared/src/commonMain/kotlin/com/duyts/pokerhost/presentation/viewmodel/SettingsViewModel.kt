package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import com.duyts.pokerhost.di.AppScope
import com.duyts.pokerhost.util.Language
import com.duyts.pokerhost.util.SettingsManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import me.tatarka.inject.annotations.Inject

@Inject
@AppScope
class SettingsViewModel(
	private val settingsManager: SettingsManager,
) : ViewModel() {
	private val _uiState = MutableStateFlow(
		SettingsUiState(
			isDarkMode = settingsManager.isDarkMode,
			language = settingsManager.language
		)
	)
	val uiState: StateFlow<SettingsUiState> = _uiState.asStateFlow()

	fun toggleDarkMode(enabled: Boolean) {
		settingsManager.isDarkMode = enabled
		_uiState.update { it.copy(isDarkMode = enabled) }
	}

	fun setLanguage(language: Language) {
		settingsManager.language = language
		_uiState.update { it.copy(language = language) }
	}
}

data class SettingsUiState(
	val isDarkMode: Boolean = false,
	val language: Language = Language.ENGLISH,
)
