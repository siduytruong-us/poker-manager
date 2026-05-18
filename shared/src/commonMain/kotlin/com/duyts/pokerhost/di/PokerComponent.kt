package com.duyts.pokerhost.di

import com.duyts.pokerhost.data.repository.PokerRepositoryImpl
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.PokerRepository
import com.duyts.pokerhost.presentation.viewmodel.DashboardViewModel
import com.duyts.pokerhost.presentation.viewmodel.EditProfileViewModel
import com.duyts.pokerhost.presentation.viewmodel.LoginViewModel
import com.duyts.pokerhost.presentation.viewmodel.MainViewModel
import com.duyts.pokerhost.presentation.viewmodel.PokerSessionDetailViewModel
import com.duyts.pokerhost.presentation.viewmodel.ProfileViewModel
import com.duyts.pokerhost.presentation.viewmodel.SettingsViewModel
import com.duyts.pokerhost.presentation.viewmodel.StartViewModel
import com.duyts.pokerhost.presentation.viewmodel.StatisticsViewModel
import me.tatarka.inject.annotations.Component
import me.tatarka.inject.annotations.KmpComponentCreate
import me.tatarka.inject.annotations.Provides

@Component
@AppScope
abstract class PokerComponent : PlatformComponent {

	@Provides
	@AppScope
	fun providePokerRepository(impl: PokerRepositoryImpl): PokerRepository = impl

	abstract val authRepository: AuthRepository

	abstract val startViewModel: StartViewModel

	abstract val loginViewModel: LoginViewModel

	abstract val mainViewModel: MainViewModel

	abstract val dashboardViewModel: DashboardViewModel

	abstract val settingsViewModel: SettingsViewModel

	abstract val statisticsViewModel: StatisticsViewModel

	abstract val profileViewModel: ProfileViewModel

	abstract val editProfileViewModel: EditProfileViewModel

	abstract val pokerSessionDetailViewModelFactory: (String) -> PokerSessionDetailViewModel

	companion object
}

@KmpComponentCreate
expect fun PokerComponent.Companion.create(): PokerComponent
