package com.duyts.pokerhost.di

import com.duyts.pokerhost.data.local.InMemoryDatabase
import com.duyts.pokerhost.data.local.PokerLocalDataSource
import com.duyts.pokerhost.data.local.PokerLocalDataSourceImpl
import com.duyts.pokerhost.data.remote.PokerRemoteDataSource
import com.duyts.pokerhost.data.repository.WasmAuthRepository
import com.duyts.pokerhost.data.repository.WasmStorageRepository
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.StorageRepository
import com.duyts.pokerhost.util.ShareManager
import com.duyts.pokerhost.util.WebShareManager
import me.tatarka.inject.annotations.Provides

actual interface PlatformComponent {
	@Provides
	@AppScope
	fun provideAuthRepository(impl: WasmAuthRepository): AuthRepository = impl

	@Provides
	@AppScope
	fun provideShareManager(impl: WebShareManager): ShareManager = impl

	@Provides
	@AppScope
	fun provideStorageRepository(impl: WasmStorageRepository): StorageRepository = impl

	@Provides
	@AppScope
	fun providePokerLocalDataSource(impl: PokerLocalDataSourceImpl): PokerLocalDataSource = impl

	@Provides
	fun providePokerRemoteDataSource(): PokerRemoteDataSource? = null

	@Provides
	@AppScope
	fun provideInMemoryDatabase(): InMemoryDatabase = InMemoryDatabase()
}
