package com.example.simplenote.ui.settings.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.user.getuser.abstraction.IUserGetter
import com.example.simplenote.modules.authentication.user.getuser.dto.response.GetUserResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class SettingsViewModel: ViewModel() {
    private val userGetter: IUserGetter = DependencyProvider.userGetter
    private val _firstName = MutableStateFlow("")
    val firstName: StateFlow<String> get() = _firstName

    private val _lastName = MutableStateFlow("")
    val lastName: StateFlow<String> get() = _lastName

    private val _email = MutableStateFlow("")
    val email: StateFlow<String> get() = _email

    private val _showLogoutDialog = MutableStateFlow(false)
    val showLogoutDialog: StateFlow<Boolean> get() = _showLogoutDialog

    private val _navigationEvent = MutableStateFlow<SettingsNavigationEvent>(SettingsNavigationEvent.None)
    val navigationEvent: StateFlow<SettingsNavigationEvent> get() = _navigationEvent

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> get() = _isLoading

    private val _errorMessage = MutableStateFlow<String?>(null)
    val errorMessage: StateFlow<String?> get() = _errorMessage

    fun loadUser(context: Context) {
        viewModelScope.launch {
            _isLoading.value = true
            _errorMessage.value = null
            val getUserResponse = userGetter.getUser(context)
            when (getUserResponse) {
                is GetUserResponse.Success -> {
                    val dto = getUserResponse.getUserResponseDto
                    _firstName.value = dto.firstName
                    _lastName.value = dto.lastName
                    _email.value = dto.email
                }
                is GetUserResponse.Failure -> {
                    _errorMessage.value = getUserResponse.exception.errors.firstOrNull()?.detail ?: "Failed to load user"
                }
            }
            _isLoading.value = false
        }
    }

    fun onBackClick() {
        _navigationEvent.value = SettingsNavigationEvent.ToHome
    }

    fun onLogoutClick() {
        _showLogoutDialog.value = true
    }

    fun onLogoutCancel() {
        _showLogoutDialog.value = false
    }

    fun onLogoutConfirm() {
        _showLogoutDialog.value = false
        ConstantProvider.refreshToken = ""
        ConstantProvider.accessToken = ""
        _navigationEvent.value = SettingsNavigationEvent.ToStart
    }

    fun onNavigationHandled() {
        _navigationEvent.value = SettingsNavigationEvent.None
    }
}