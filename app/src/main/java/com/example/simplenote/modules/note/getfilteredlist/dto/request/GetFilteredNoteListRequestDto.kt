package com.example.simplenote.modules.note.getfilteredlist.dto.request

data class GetFilteredNoteListRequestDto(
    val page: Int,
    val pageSize: Int,
    val title: String?,
    val description: String?
)
