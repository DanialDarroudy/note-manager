package com.example.simplenote.modules.note.getlist.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class GetNoteListResponse {
    data class Success(val getNoteListResponseDto: GetNoteListResponseDto) : GetNoteListResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : GetNoteListResponse()
}