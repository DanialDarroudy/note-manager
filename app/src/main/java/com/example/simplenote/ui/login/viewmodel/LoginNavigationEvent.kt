package com.example.simplenote.ui.login.viewmodel

sealed class LoginNavigationEvent {
    object None : LoginNavigationEvent()
    object ToRegister : LoginNavigationEvent()
    object ToHome : LoginNavigationEvent()
}