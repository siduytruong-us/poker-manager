package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.domain.model.AppConfig
import com.duyts.pokerhost.domain.repository.AppConfigRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import me.tatarka.inject.annotations.Inject

@Inject
class WasmAppConfigRepository : AppConfigRepository {
	override fun getAppConfig(): Flow<AppConfig> {
		return flowOf(
			AppConfig(
				androidBannerAdId = "ca-app-pub-3940256099942544/6300978111",
				iosBannerAdId = "ca-app-pub-3940256099942544/2934735716"
			)
		)
	}
}
