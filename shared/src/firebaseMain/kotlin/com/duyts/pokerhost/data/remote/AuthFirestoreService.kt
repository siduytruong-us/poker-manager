package com.duyts.pokerhost.data.remote

import com.duyts.pokerhost.util.ClockUtils
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import me.tatarka.inject.annotations.Inject

@Inject
class AuthFirestoreService {
	private val firestore = Firebase.firestore
	private val usersCollection = firestore.collection("users")

	fun getUserSnapshot(userId: String) = usersCollection.document(userId).snapshots()

	suspend fun getUser(userId: String) = usersCollection.document(userId).get()

	suspend fun createUser(
        userId: String,
        email: String?,
        displayName: String?,
        photoUrl: String?,
    ) {
		val userRef = usersCollection.document(userId)
		val doc = userRef.get()
		if (!doc.exists) {
			val firestoreUser = FirestoreUser(
				id = userId,
				email = email,
				displayName = displayName ?: "User ${userId.takeLast(4)}",
				photoUrl = photoUrl,
				createdAt = ClockUtils.now().toEpochMilliseconds()
			)
			userRef.set(FirestoreUser.serializer(), firestoreUser)
		}
	}

	suspend fun updateProfile(userId: String, displayName: String?, photoUrl: String?) {
		val userRef = usersCollection.document(userId)
		val updates = mutableMapOf<String, Any?>()
		if (displayName != null) updates["displayName"] = displayName
		if (photoUrl != null) updates["photoUrl"] = photoUrl

		if (updates.isNotEmpty()) {
			userRef.update(updates)
		}
	}
}
