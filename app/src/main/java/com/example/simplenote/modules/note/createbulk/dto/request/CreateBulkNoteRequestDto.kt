package com.example.simplenote.modules.note.createbulk.dto.request

import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto
import kotlinx.serialization.Serializable

@Serializable
data class CreateBulkNoteRequestDto(
    val requests: List<CreateNoteRequestDto>
)