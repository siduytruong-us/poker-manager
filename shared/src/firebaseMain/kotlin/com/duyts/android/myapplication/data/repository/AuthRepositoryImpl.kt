package com.duyts.android.myapplication.data.repository

import com.duyts.android.myapplication.data.remote.FirestoreUser
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.domain.repository.AuthUser
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.auth.FirebaseAuth
import dev.gitlive.firebase.auth.FirebaseUser
import dev.gitlive.firebase.auth.GoogleAuthProvider
import dev.gitlive.firebase.auth.auth
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import me.tatarka.inject.annotations.Inject
import kotlinx.datetime.Clock

@Inject
@OptIn(ExperimentalCoroutinesApi::class)
class AuthRepositoryImpl : AuthRepository {
    private val auth: FirebaseAuth = Firebase.auth
    private val firestore = Firebase.firestore

    override val currentUser: Flow<AuthUser?> = auth.authStateChanged.flatMapLatest { firebaseUser ->
        if (firebaseUser == null) {
            flowOf(null)
        } else {
            firestore.collection("users").document(firebaseUser.uid)
                .snapshots()
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
                        // Fallback to Firebase Auth info if Firestore profile doesn't exist yet
                        AuthUser(
                            id = firebaseUser.uid,
                            email = firebaseUser.email,
                            displayName = firebaseUser.displayName,
                            photoUrl = firebaseUser.photoURL
                        )
                    }
                }
        }
    }

    override suspend fun signInWithGoogle(idToken: String): Result<Unit> = runCatching {
        val authResult = auth.signInWithCredential(GoogleAuthProvider.credential(idToken, null))
        val user = authResult.user
        if (user != null) {
            createUser(user)
        }
    }

    private suspend fun createUser(user: FirebaseUser) {
        val userRef = Firebase.firestore.collection("users").document(user.uid)
        val doc = userRef.get()
        if (!doc.exists) {
            val firestoreUser = FirestoreUser(
                id = user.uid,
                email = user.email,
                displayName = user.displayName ?: "User ${user.uid.takeLast(4)}",
                photoUrl = user.photoURL,
                createdAt = Clock.System.now().toEpochMilliseconds()
            )
            userRef.set(FirestoreUser.serializer(), firestoreUser)
        }
    }

    override suspend fun signOut() {
        auth.signOut()
    }

    override suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit> = runCatching {
        val user = auth.currentUser ?: throw Exception("User not logged in")
        
        // Update Firebase Auth profile
        user.updateProfile(displayName, photoUrl)
        
        // Update Firestore profile
        val userRef = firestore.collection("users").document(user.uid)
        val updates = mutableMapOf<String, Any?>()
        if (displayName != null) updates["displayName"] = displayName
        if (photoUrl != null) updates["photoUrl"] = photoUrl
        
        if (updates.isNotEmpty()) {
            userRef.update(updates)
        }
    }
}
