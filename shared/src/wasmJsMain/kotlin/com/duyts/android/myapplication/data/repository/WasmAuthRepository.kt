package com.duyts.android.myapplication.data.repository

import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.AuthUser
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import me.tatarka.inject.annotations.Inject
import kotlin.js.ExperimentalWasmJsInterop
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(idToken, onSuccess, onError) => { firebase.auth().signInWithCredential(firebase.auth.GoogleAuthProvider.credential(idToken)).then(() => onSuccess()).catch((e) => onError(e.message)); }")
external fun firebaseSignInWithGoogle(idToken: String, onSuccess: () -> Unit, onError: (String) -> Unit)

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("() => firebase.auth().signOut()")
external fun firebaseSignOut()

@OptIn(ExperimentalWasmJsInterop::class)
@JsFun("(callback) => { firebase.auth().onAuthStateChanged((user) => { if (user) { callback(user.uid, user.email, user.displayName); } else { callback(null, null, null); } }); }")
external fun firebaseOnAuthStateChanged(callback: (String?, String?, String?) -> Unit)

@Inject
class WasmAuthRepository : AuthRepository {
    private val _currentUser = MutableStateFlow<AuthUser?>(null)
    override val currentUser: Flow<AuthUser?> = _currentUser

    init {
        firebaseOnAuthStateChanged { uid, email, displayName ->
            _currentUser.value = if (uid != null) {
                AuthUser(
                    id = uid,
                    email = email,
                    displayName = displayName
                )
            } else {
                null
            }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = suspendCancellableCoroutine { continuation ->
        firebaseSignInWithGoogle(idToken, {
            continuation.resume(Result.success(Unit))
        }, { error ->
            continuation.resume(Result.failure(Exception(error)))
        })
    }

    override suspend fun signOut() {
        firebaseSignOut()
    }
}
