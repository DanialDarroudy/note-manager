package com.example.simplenote.modules.note.common.model

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class NoteResponse {
    data class Success(val note: Note?) : NoteResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : NoteResponse()
}