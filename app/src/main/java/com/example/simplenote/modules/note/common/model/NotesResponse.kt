package com.example.simplenote.modules.note.common.model

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class NotesResponse {
    data class Success(val notes: Notes) : NotesResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : NotesResponse()
}