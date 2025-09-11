package com.example.simplenote.modules.note.create.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto

interface INoteCreator {
    suspend fun createNote(createNoteRequestDto: CreateNoteRequestDto, context: Context): NoteResponse
}