package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.data.remote.AuthFirestoreService
import com.duyts.pokerhost.data.remote.FirestoreUser
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImpl(
	private val firestoreService: AuthFirestoreService,
) : AuthRepository {
	private val auth: FirebaseAuth = Firebase.auth

	override val currentUser: Flow<AuthUser?> =
		auth.authStateChanged.flatMapLatest { firebaseUser ->
			if (firebaseUser == null) {
				flowOf(null)
			} else {
				firestoreService.getUserSnapshot(firebaseUser.uid)
					.map { snapshot ->
						if (snapshot.exists) {
							val fsUser = snapshot.data<FirestoreUser>()
							AuthUser(
								id = fsUser.id,
								email = fsUser.email,
								displayName = fsUser.displayName,
								photoUrl = fsUser.photoUrl
							)
						} else {
							null
						}
					}
			}
		}

	override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> =
		runCatching {
			val authResult =
				auth.signInWithCredential(GoogleAuthProvider.credential(idToken, accessToken))
		val user = authResult.user
		if (user != null) {
			firestoreService.createUser(
				userId = user.uid,
				email = user.email,
				displayName = user.displayName,
				photoUrl = user.photoURL
			)
		}
	}

	override suspend fun signOut() {
		auth.signOut()
	}

	override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> =
		runCatching {
			val user = auth.currentUser ?: throw Exception("User not logged in")

			// Update Firebase Auth profile
			user.updateProfile(displayName, photoUrl)

			// Update Firestore profile
			firestoreService.updateProfile(user.uid, displayName, photoUrl)
		}
}
