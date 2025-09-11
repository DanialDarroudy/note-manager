package com.example.simplenote.modules.note.createbulk.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class CreateBulkNoteResponse {
    data class Success(val createBulkNoteResponseDto: CreateBulkNoteResponseDto) : CreateBulkNoteResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : CreateBulkNoteResponse()
}