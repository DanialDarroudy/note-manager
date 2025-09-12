package com.example.simplenote.ui.settings.viewmodel

sealed class SettingsNavigationEvent {
    object None : SettingsNavigationEvent()
    object ToHome : SettingsNavigationEvent()
    object ToStart : SettingsNavigationEvent()
}