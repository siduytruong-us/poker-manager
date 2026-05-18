package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.domain.repository.StorageRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.storage.FirebaseStorage
import dev.gitlive.firebase.storage.storage
import me.tatarka.inject.annotations.Inject

@Inject
class FirebaseStorageRepository : StorageRepository {
	private val storage: FirebaseStorage = Firebase.storage

	override suspend fun uploadProfilePicture(userId: String, bytes: ByteArray): Result<String> =
		runCatching {
			val ref = storage.reference("profile_pictures/$userId.jpg")
			ref.putData(bytes.toData())
			ref.getDownloadUrl()
		}
}
