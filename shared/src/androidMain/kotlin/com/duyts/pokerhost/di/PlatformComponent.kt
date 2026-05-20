package com.duyts.pokerhost.di

import com.duyts.pokerhost.data.local.InMemoryDatabase
import com.duyts.pokerhost.data.local.PokerLocalDataSource
import com.duyts.pokerhost.data.local.PokerLocalDataSourceImpl
import com.duyts.pokerhost.data.remote.FirestorePokerDataSource
import com.duyts.pokerhost.data.remote.PokerRemoteDataSource
import com.duyts.pokerhost.data.repository.AuthRepositoryImpl
import com.duyts.pokerhost.data.repository.FirebaseStorageRepository
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.StorageRepository
import com.duyts.pokerhost.util.AndroidShareManager
import com.duyts.pokerhost.util.ShareManager
import me.tatarka.inject.annotations.Provides

actual interface PlatformComponent {
	@Provides
	@AppScope
	fun provideAuthRepository(impl: AuthRepositoryImpl): AuthRepository = impl

	@Provides
	@AppScope
	fun provideShareManager(impl: AndroidShareManager): ShareManager = impl

	@Provides
	@AppScope
	fun provideStorageRepository(impl: FirebaseStorageRepository): StorageRepository = impl

	@Provides
	@AppScope
	fun providePokerLocalDataSource(impl: PokerLocalDataSourceImpl): PokerLocalDataSource = impl

	@Provides
	@AppScope
	fun providePokerRemoteDataSource(impl: FirestorePokerDataSource): PokerRemoteDataSource? = impl

	@Provides
	@AppScope
	fun provideInMemoryDatabase(): InMemoryDatabase = InMemoryDatabase()
}
