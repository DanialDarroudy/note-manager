package com.example.simplenote.ui.register.viewmodel

sealed class RegisterNavigationEvent {
    object None : RegisterNavigationEvent()
    object ToLogin : RegisterNavigationEvent()
    object ToHome : RegisterNavigationEvent()
}