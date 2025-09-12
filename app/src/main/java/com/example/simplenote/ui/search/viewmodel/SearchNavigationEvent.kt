package com.example.simplenote.ui.search.viewmodel

sealed class SearchNavigationEvent {
    object None : SearchNavigationEvent()
    object ToHome : SearchNavigationEvent()
    data class ToNote(val id: Long) : SearchNavigationEvent()
}