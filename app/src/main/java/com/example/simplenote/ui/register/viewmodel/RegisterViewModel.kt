package com.example.simplenote.ui.register.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.modules.authentication.token.createtoken.abstraction.ITokenCreator
import com.example.simplenote.modules.authentication.token.createtoken.dto.request.CreateTokenRequestDto
import com.example.simplenote.modules.authentication.token.createtoken.dto.response.CreateTokenResponse
import com.example.simplenote.modules.authentication.user.createuser.abstraction.IUserCreator
import com.example.simplenote.modules.authentication.user.createuser.dto.request.CreateUserRequestDto
import com.example.simplenote.modules.authentication.user.createuser.dto.response.CreateUserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class RegisterViewModel: ViewModel() {
    private val userCreator: IUserCreator = DependencyProvider.userCreator
    private val tokenCreator: ITokenCreator = DependencyProvider.tokenCreator
    private val _navigationEvent = MutableStateFlow<RegisterNavigationEvent>(RegisterNavigationEvent.None)
    val navigationEvent: StateFlow<RegisterNavigationEvent> get() = _navigationEvent
    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun onBackToLoginClicked() {
        _navigationEvent.value = RegisterNavigationEvent.ToLogin
    }

    fun registerUser(firstName: String, lastName: String, username: String, email: String, password: String, confirmPassword: String, context: Context) {
        _errorMessage.value = null
        if (password != confirmPassword) {
            _errorMessage.value = "Passwords do not match."
            return
        }

        if (firstName.isBlank() || lastName.isBlank() || username.isBlank() || email.isBlank() || password.isBlank()) {
            _errorMessage.value = "Please fill in all fields."
            return
        }

        viewModelScope.launch {
            val request = CreateUserRequestDto(userName = username, passWord = password, email = email, firstName = firstName, lastName = lastName)
            val response = userCreator.createUser(request, context)
            when (response) {
                is CreateUserResponse.Success -> {
                    val request = CreateTokenRequestDto(username, password)
                    val response = tokenCreator.createToken(request, context)
                    when (response) {
                        is CreateTokenResponse.Success -> {
                            _navigationEvent.value = RegisterNavigationEvent.ToHome
                        }
                        is CreateTokenResponse.Failure -> {
                            _errorMessage.value = response.exception.errors.firstOrNull()?.detail ?: "Token creation is failed"
                        }
                    }
                }
                is CreateUserResponse.Failure -> {
                    _errorMessage.value = response.exception.errors.firstOrNull()?.detail ?: "Registration failed."
                }
            }
        }
    }

    fun onNavigationHandled() {
        _navigationEvent.value = RegisterNavigationEvent.None
        _errorMessage.value = null
    }
}