package com.duyts.pokerhost.util

import com.duyts.pokerhost.di.AppScope
import com.russhwolf.settings.Settings
import me.tatarka.inject.annotations.Inject

@AppScope
@Inject
class SettingsManager {
	private val settings: Settings = Settings()

	companion object {
		private const val KEY_DARK_MODE = "dark_mode"
		private const val KEY_LANGUAGE = "language"
		private const val KEY_FIRST_TIME = "first_time"
	}

	var isFirstTime: Boolean
		get() = settings.getBoolean(KEY_FIRST_TIME, true)
		set(value) {
			settings.putBoolean(KEY_FIRST_TIME, value)
		}

	var isDarkMode: Boolean
		get() = settings.getBoolean(KEY_DARK_MODE, false)
		set(value) {
			settings.putBoolean(KEY_DARK_MODE, value)
		}

	var language: Language
		get() {
			val code = settings.getString(KEY_LANGUAGE, Language.ENGLISH.code)
			return Language.entries.find { it.code == code } ?: Language.ENGLISH
		}
		set(value) {
			settings.putString(KEY_LANGUAGE, value.code)
		}
}
