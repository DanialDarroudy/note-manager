package com.example.simplenote.ui.note.activity

data class NoteUiState(
    val noteId: Long? = null,
    val title: String = "",
    val description: String = "",
    val updatedAt: String = "",
    val isLoading: Boolean = false,
    var errorMessage: String? = null,
    val showDeleteDialog: Boolean = false
)