package com.example.simplenote.modules.note.update.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.update.dto.request.UpdateNoteRequestDto

interface INoteUpdater {
    suspend fun updateNote(updateNoteRequestDto: UpdateNoteRequestDto, context: Context): NoteResponse
}