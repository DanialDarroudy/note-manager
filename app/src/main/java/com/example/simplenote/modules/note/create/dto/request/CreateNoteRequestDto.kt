package com.example.simplenote.modules.note.create.dto.request

import kotlinx.serialization.Serializable

@Serializable
data class CreateNoteRequestDto(
    val title: String = "",
    val description: String = ""
)