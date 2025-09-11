package com.example.simplenote.ui.login.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.modules.authentication.token.createtoken.dto.request.CreateTokenRequestDto
import com.example.simplenote.modules.authentication.token.createtoken.dto.response.CreateTokenResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class LoginViewModel: ViewModel() {
    private val tokenCreator = DependencyProvider.tokenCreator
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage
    private val _navigationEvent = MutableStateFlow<LoginNavigationEvent>(LoginNavigationEvent.None)
    val navigationEvent: StateFlow<LoginNavigationEvent> get() = _navigationEvent
    fun login(username: String, password: String, context: Context) {
        _errorMessage.value = null
        if (username.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please enter both username and password."
            return
        }
        viewModelScope.launch {
            val request = CreateTokenRequestDto(username, password)
            val response = tokenCreator.createToken(request, context)
            when (response) {
                is CreateTokenResponse.Success -> {
                    _navigationEvent.value = LoginNavigationEvent.ToHome
                }
                is CreateTokenResponse.Failure -> {
                    _errorMessage.value = response.exception.errors.firstOrNull()?.detail ?: "Login failed"
                }
            }
        }
    }

    fun register(){
        _navigationEvent.value = LoginNavigationEvent.ToRegister
    }

    fun onNavigationHandled() {
        _navigationEvent.value = LoginNavigationEvent.None
        _errorMessage.value = null
    }
}