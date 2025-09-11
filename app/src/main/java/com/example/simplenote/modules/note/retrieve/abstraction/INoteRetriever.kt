package com.example.simplenote.modules.note.retrieve.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.retrieve.dto.request.RetrieveNoteRequestDto

interface INoteRetriever {
    suspend fun retrieveNote(retrieveNoteRequestDto: RetrieveNoteRequestDto, context: Context): NoteResponse
}