package com.example.simplenote.modules.note.createbulk.abstraction

import android.content.Context
import com.example.simplenote.modules.note.createbulk.dto.request.CreateBulkNoteRequestDto
import com.example.simplenote.modules.note.createbulk.dto.response.CreateBulkNoteResponse

interface IBulkNoteCreator {
    suspend fun createBulkNote(createBulkNoteRequestDto: CreateBulkNoteRequestDto, context: Context) : CreateBulkNoteResponse
}