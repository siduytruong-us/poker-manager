package com.duyts.android.myapplication.presentation.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.duyts.android.myapplication.domain.repository.AuthRepository
import com.duyts.android.myapplication.presentation.navigation.Route
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import me.tatarka.inject.annotations.Inject

@Inject
class SplashViewModel(
    private val authRepository: AuthRepository
) : ViewModel() {

    private val _navigationEvent = MutableSharedFlow<Route>()
    val navigationEvent: SharedFlow<Route> = _navigationEvent.asSharedFlow()

    init {
        checkAuth()
    }

    private fun checkAuth() {
        viewModelScope.launch {
            delay(1000) // Minimum display time for Splash
            val user = authRepository.currentUser.first()
            if (user == null) {
                _navigationEvent.emit(Route.Login)
            } else {
                _navigationEvent.emit(Route.Main())
            }
        }
    }
}
