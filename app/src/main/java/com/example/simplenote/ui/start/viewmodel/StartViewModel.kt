package com.example.simplenote.ui.start.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow

class StartViewModel: ViewModel() {
    private val _navigateToLogin = MutableStateFlow(false)
    val navigateToLogin: StateFlow<Boolean> get() = _navigateToLogin

    fun onGetStartedClicked() {
        _navigateToLogin.value = true
    }

    fun onNavigationHandled() {
        _navigateToLogin.value = false
    }
}