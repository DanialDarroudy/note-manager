package com.example.simplenote.modules.note.destroy.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.destroy.dto.request.DestroyNoteRequestDto

interface INoteDestroyer {
    suspend fun destroyNote(destroyNoteRequestDto: DestroyNoteRequestDto, context: Context): NoteResponse
}