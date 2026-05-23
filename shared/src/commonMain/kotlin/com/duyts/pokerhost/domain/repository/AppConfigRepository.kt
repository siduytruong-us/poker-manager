package com.duyts.pokerhost.domain.repository

import com.duyts.pokerhost.domain.model.AppConfig
import kotlinx.coroutines.flow.Flow

interface AppConfigRepository {
	fun getAppConfig(): Flow<AppConfig>
}
