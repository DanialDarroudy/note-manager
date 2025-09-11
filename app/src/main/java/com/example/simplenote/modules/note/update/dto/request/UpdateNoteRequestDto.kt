package com.example.simplenote.modules.note.update.dto.request

import kotlinx.serialization.*

@Serializable
data class UpdateNoteRequestDto(
    @Transient val id: Long = -1,
    val title: String,
    val description: String
)
