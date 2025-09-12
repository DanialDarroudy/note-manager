package com.example.simplenote.ui.note.viewmodel

sealed class NoteNavigationEvent {
    object None : NoteNavigationEvent()
    object ToHome : NoteNavigationEvent()
}