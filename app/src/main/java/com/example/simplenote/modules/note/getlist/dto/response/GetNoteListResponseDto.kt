package com.example.simplenote.modules.note.getlist.dto.response

import com.example.simplenote.modules.note.common.model.Note

data class GetNoteListResponseDto(
    val notes: List<Note>
)