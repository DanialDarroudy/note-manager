package com.example.simplenote.modules.note.getlist.abstraction

import android.content.Context
import com.example.simplenote.modules.note.getlist.dto.response.GetNoteListResponse

interface INoteListGetter {
    suspend fun getNoteList(context: Context): GetNoteListResponse
}