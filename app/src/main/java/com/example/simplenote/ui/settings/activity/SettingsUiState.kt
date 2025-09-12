package com.example.simplenote.ui.settings.activity

data class SettingsUiState(
    val firstName: String = "",
    val lastName: String = "",
    val email: String = "",
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val showLogoutDialog: Boolean = false
)