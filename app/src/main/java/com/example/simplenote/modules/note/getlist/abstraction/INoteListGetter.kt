package com.example.simplenote.modules.note.getlist.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NotesResponse
import com.example.simplenote.modules.note.getlist.dto.request.GetNoteListRequestDto

interface INoteListGetter {
    suspend fun getNoteList(getNoteListRequestDto: GetNoteListRequestDto, context: Context): NotesResponse
}