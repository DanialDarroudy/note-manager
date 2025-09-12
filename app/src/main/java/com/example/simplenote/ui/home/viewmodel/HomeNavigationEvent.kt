package com.example.simplenote.ui.home.viewmodel

import com.example.simplenote.modules.note.common.model.Note

sealed class HomeNavigationEvent {
    object None : HomeNavigationEvent()
    object ToSettings : HomeNavigationEvent()
    data class ToNote(val id: Long?) : HomeNavigationEvent()
    data class ToSearch(val notes: List<Note>): HomeNavigationEvent()
}