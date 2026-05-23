package com.duyts.pokerhost.domain.usecase

import com.duyts.pokerhost.domain.repository.AppConfigRepository
import me.tatarka.inject.annotations.Inject

@Inject
class GetAppConfigUseCase(private val repository: AppConfigRepository) {
	operator fun invoke() = repository.getAppConfig()
}
