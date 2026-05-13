package com.duyts.android.myapplication.data.repository

import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject

@Inject
class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth

    override val currentUser: Flow<AuthUser?> = auth.authStateChanged.map { user ->
        user?.let {
            AuthUser(
                id = it.uid,
                email = it.email,
                displayName = it.displayName
            )
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        auth.signInWithCredential(GoogleAuthProvider.credential(idToken, null))
    }

    override suspend fun signOut() {
        auth.signOut()
    }
}
