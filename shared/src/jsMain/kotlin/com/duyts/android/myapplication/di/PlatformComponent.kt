package com.duyts.android.myapplication.di

import com.duyts.android.myapplication.data.local.InMemoryDatabase
import com.duyts.android.myapplication.data.repository.AuthRepositoryImpl
import com.duyts.android.myapplication.data.repository.FirebaseStorageRepository
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.StorageRepository
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
