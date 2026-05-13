package com.duyts.android.myapplication.di

import com.duyts.android.myapplication.data.local.PokerLocalDataSource
import com.duyts.android.myapplication.data.local.PokerLocalDataSourceImpl
import com.duyts.android.myapplication.data.repository.PokerRepositoryImpl
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.PokerRepository
import com.duyts.android.myapplication.presentation.viewmodel.LoginViewModel
import com.duyts.android.myapplication.presentation.viewmodel.PokerSessionDetailViewModel
import com.duyts.android.myapplication.presentation.viewmodel.PokerSessionListViewModel
import com.duyts.android.myapplication.presentation.viewmodel.SettingsViewModel
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

    abstract val loginViewModel: LoginViewModel

    abstract val pokerSessionListViewModel: PokerSessionListViewModel

    abstract val settingsViewModel: SettingsViewModel
    
    abstract val pokerSessionDetailViewModelFactory: (String) -> PokerSessionDetailViewModel

    companion object
}

@KmpComponentCreate
expect fun PokerComponent.Companion.create(): PokerComponent
