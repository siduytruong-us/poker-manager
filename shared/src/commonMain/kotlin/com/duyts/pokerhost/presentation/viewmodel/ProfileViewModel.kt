package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.AuthUser
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class ProfileViewModel(
	private val authRepository: AuthRepository,
) : ViewModel() {

	val uiState: StateFlow<ProfileUiState> = authRepository.currentUser
		.map { user -> ProfileUiState(user = user) }
		.stateIn(
			scope = viewModelScope,
			started = SharingStarted.WhileSubscribed(5000),
			initialValue = ProfileUiState()
		)

	fun logout() {
		viewModelScope.launch {
			authRepository.signOut()
		}
	}
}

data class ProfileUiState(
	val user: AuthUser? = null,
)
