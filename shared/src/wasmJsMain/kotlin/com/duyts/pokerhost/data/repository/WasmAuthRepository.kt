package com.duyts.pokerhost.data.repository

import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import me.tatarka.inject.annotations.Inject
import kotlin.coroutines.resume

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(idToken, onSuccess, onError) => { firebase.auth().signInWithCredential(firebase.auth.GoogleAuthProvider.credential(idToken)).then(() => onSuccess()).catch((e) => onError(e.message)); }")
external fun firebaseSignInWithGoogle(
	idToken: String,
	onSuccess: () -> Unit,
	onError: (String) -> Unit,
)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => firebase.auth().signOut()")
external fun firebaseSignOut()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(callback) => { firebase.auth().onAuthStateChanged((user) => { if (user) { callback(user.uid, user.email, user.displayName, user.photoURL); } else { callback(null, null, null, null); } }); }")
external fun firebaseOnAuthStateChanged(callback: (String?, String?, String?, String?) -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(displayName, photoUrl, onSuccess, onError) => { firebase.auth().currentUser.updateProfile({ displayName: displayName, photoURL: photoUrl }).then(() => onSuccess()).catch((e) => onError(e.message)); }")
external fun firebaseUpdateProfile(
	displayName: String?,
	photoUrl: String?,
	onSuccess: () -> Unit,
	onError: (String) -> Unit,
)

@Inject
class WasmAuthRepository : AuthRepository {
	private val _currentUser = MutableStateFlow<AuthUser?>(null)
	override val currentUser: Flow<AuthUser?> = _currentUser

	init {
		firebaseOnAuthStateChanged { uid, email, displayName, photoUrl ->
			_currentUser.value = if (uid != null) {
				AuthUser(
					id = uid,
					email = email,
					displayName = displayName,
					photoUrl = photoUrl
				)
			} else {
				null
			}
		}
	}

	override suspend fun signInWithGoogle(idToken: String, accessToken: String?): Result<Unit> =
		suspendCancellableCoroutine { continuation ->
			firebaseSignInWithGoogle(idToken, {
				continuation.resume(Result.success(Unit))
			}, { error ->
				continuation.resume(Result.failure(Exception(error)))
			})
		}

	override suspend fun signOut() {
		firebaseSignOut()
	}

	override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> =
		suspendCancellableCoroutine { continuation ->
			firebaseUpdateProfile(displayName, photoUrl, {
				continuation.resume(Result.success(Unit))
			}, { error ->
				continuation.resume(Result.failure(Exception(error)))
			})
		}
}
