package com.example.simplenote.ui.home.viewmodel

sealed class HomeNavigationEvent {
    object None : HomeNavigationEvent()
    object ToSettings : HomeNavigationEvent()
    data class ToNote(val id: Long?) : HomeNavigationEvent()
}