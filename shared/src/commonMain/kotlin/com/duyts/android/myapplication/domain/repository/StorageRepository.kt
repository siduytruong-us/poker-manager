package com.duyts.android.myapplication.domain.repository

interface StorageRepository {
    suspend fun uploadProfilePicture(userId: String, bytes: ByteArray): Result<String>
}
