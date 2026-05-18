package com.duyts.pokerhost.di

import com.duyts.pokerhost.data.local.InMemoryDatabase
import com.duyts.pokerhost.data.repository.AuthRepositoryImpl
import com.duyts.pokerhost.data.repository.FirebaseStorageRepository
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.StorageRepository
import me.tatarka.inject.annotations.Provides

actual interface PlatformComponent {
	@Provides
	@AppScope
	fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

	@Provides
	@AppScope
	fun provideStorageRepository(impl: FirebaseStorageRepository): StorageRepository = impl

	@Provides
	@AppScope
	fun provideInMemoryDatabase(): InMemoryDatabase = InMemoryDatabase()
}
