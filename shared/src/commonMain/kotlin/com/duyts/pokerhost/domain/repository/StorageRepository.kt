package com.duyts.pokerhost.domain.repository

interface StorageRepository {
	suspend fun uploadProfilePicture(userId: String, bytes: ByteArray): Result<String>
}
