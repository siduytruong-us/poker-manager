package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.domain.model.AppConfig
import com.duyts.pokerhost.domain.repository.AppConfigRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.remoteconfig.remoteConfig
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import me.tatarka.inject.annotations.Inject

@Inject
class AppConfigRepositoryImpl : AppConfigRepository {
	private val remoteConfig = Firebase.remoteConfig

	override fun getAppConfig(): Flow<AppConfig> = flow {
		emit(getCurrentConfig())

		runCatching { remoteConfig.fetchAndActivate() }
			.onSuccess { emit(getCurrentConfig()) }
	}

	private fun getCurrentConfig() = AppConfig(
		androidBannerAdId = getString(KEY_ANDROID_AD_ID, DEFAULT_ANDROID_AD_ID),
		iosBannerAdId = getString(KEY_IOS_AD_ID, DEFAULT_IOS_AD_ID),
		minVersionCode = remoteConfig.getValue(KEY_MIN_VERSION).asLong().toInt(),
		isMaintenanceMode = remoteConfig.getValue(KEY_MAINTENANCE_MODE).asBoolean()
	)

	private fun getString(key: String, default: String): String =
		remoteConfig.getValue(key).asString().ifBlank { default }

	companion object {
		private const val KEY_ANDROID_AD_ID = "androidBannerAdId"
		private const val KEY_IOS_AD_ID = "iosBannerAdId"
		private const val KEY_MIN_VERSION = "minVersionCode"
		private const val KEY_MAINTENANCE_MODE = "isMaintenanceMode"

		private const val DEFAULT_ANDROID_AD_ID = "ca-app-pub-2953073229838997/4239209047"
		private const val DEFAULT_IOS_AD_ID = "ca-app-pub-3940256099942544/2934735716"
	}
}
