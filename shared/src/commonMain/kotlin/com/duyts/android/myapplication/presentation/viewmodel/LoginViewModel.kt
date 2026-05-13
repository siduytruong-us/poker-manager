package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.repository.AuthRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class LoginViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {
    private val _loginState = MutableStateFlow<LoginState>(LoginState.Checking)
    val loginState: StateFlow<LoginState> = _loginState

    init {
        checkAutoLogin()
    }

    private fun checkAutoLogin() {
        viewModelScope.launch {
            val user = authRepository.currentUser.first()
            if (user != null) {
                _loginState.value = LoginState.Success
            } else {
                _loginState.value = LoginState.Idle
            }
        }
    }

    fun onGoogleSignInSuccess(idToken: String) {
        viewModelScope.launch {
            _loginState.value = LoginState.Loading
            authRepository.signInWithGoogle(idToken)
                .onSuccess {
                    _loginState.value = LoginState.Success
                }
                .onFailure {
                    _loginState.value = LoginState.Error(it.message ?: "Unknown error")
                }
        }
    }
}

sealed class LoginState {
    object Checking : LoginState()
    object Idle : LoginState()
    object Loading : LoginState()
    object Success : LoginState()
    data class Error(val message: String) : LoginState()
}
