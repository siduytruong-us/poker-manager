package com.duyts.pokerhost.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.pokerhost.domain.repository.AuthRepository
import com.duyts.pokerhost.domain.repository.AuthUser
import com.duyts.pokerhost.domain.repository.StorageRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class EditProfileViewModel(
	private val authRepository: AuthRepository,
	private val storageRepository: StorageRepository,
) : ViewModel() {

	private val _uiState = MutableStateFlow<EditProfileUiState>(EditProfileUiState.Idle)
	val uiState: StateFlow<EditProfileUiState> = _uiState

	val user: StateFlow<AuthUser?> = authRepository.currentUser
		.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

	fun updateProfile(displayName: String, photoUrl: String?, imageBytes: ByteArray? = null) {
		viewModelScope.launch {
			_uiState.value = EditProfileUiState.Loading

			val finalPhotoUrl = if (imageBytes != null) {
				val userId = user.value?.id ?: return@launch
				storageRepository.uploadProfilePicture(userId, imageBytes)
					.getOrElse {
						_uiState.value =
							EditProfileUiState.Error("Failed to upload image: ${it.message}")
						return@launch
					}
			} else {
				photoUrl
			}

			authRepository.updateProfile(displayName, finalPhotoUrl)
				.onSuccess {
					_uiState.value = EditProfileUiState.Success
				}
				.onFailure {
					_uiState.value = EditProfileUiState.Error(it.message ?: "Unknown error")
				}
		}
	}

	fun resetState() {
		_uiState.value = EditProfileUiState.Idle
	}
}

sealed class EditProfileUiState {
	object Idle : EditProfileUiState()
	object Loading : EditProfileUiState()
	object Success : EditProfileUiState()
	data class Error(val message: String) : EditProfileUiState()
}
