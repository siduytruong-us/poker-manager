package com.duyts.android.myapplication.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    val currentUser: Flow<AuthUser?>
    suspend fun signInWithGoogle(idToken: String): Result<Unit>
    suspend fun signOut()
    suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
}

data class AuthUser(
    val id: String,
    val email: String?,
    val displayName: String?,
    val photoUrl: String? = null
)
