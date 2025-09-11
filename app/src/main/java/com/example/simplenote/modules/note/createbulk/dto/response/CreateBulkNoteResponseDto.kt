package com.example.simplenote.modules.note.createbulk.dto.response

import com.example.simplenote.modules.note.common.model.Note

data class CreateBulkNoteResponseDto(
    val responses: List<Note>
)