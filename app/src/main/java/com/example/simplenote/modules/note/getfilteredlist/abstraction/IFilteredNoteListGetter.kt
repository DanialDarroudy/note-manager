package com.example.simplenote.modules.note.getfilteredlist.abstraction

import android.content.Context
import com.example.simplenote.modules.note.common.model.NotesResponse
import com.example.simplenote.modules.note.getfilteredlist.dto.request.GetFilteredNoteListRequestDto

interface IFilteredNoteListGetter {
    suspend fun getFilteredNoteList(getFilteredNoteListRequestDto: GetFilteredNoteListRequestDto, context: Context): NotesResponse
}