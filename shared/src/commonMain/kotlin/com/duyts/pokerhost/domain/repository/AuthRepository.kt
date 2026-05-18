package com.duyts.pokerhost.domain.repository

import kotlinx.coroutines.flow.Flow

interface AuthRepository {
	val currentUser: Flow<AuthUser?>
	suspend fun signInWithGoogle(idToken: String): Result<Unit>
	suspend fun signOut()
	suspend fun updateProfile(displayName: String?, photoUrl: String?): Result<Unit>
}

data class SessionPerformance(
	val sessionId: String,
	val sessionTitle: String,
	val completedAt: Long,
	val profit: Float,
	val buyIn: Float,
	val cashOut: Float,
	val adjustment: Float,
)

data class AuthUser(
	val id: String,
	val email: String?,
	val displayName: String?,
	val photoUrl: String? = null,
)
