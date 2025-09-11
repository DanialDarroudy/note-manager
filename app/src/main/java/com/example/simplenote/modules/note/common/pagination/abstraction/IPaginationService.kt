package com.example.simplenote.modules.note.common.pagination.abstraction

import com.example.simplenote.modules.note.common.database.entity.NoteEntity
import com.example.simplenote.modules.note.common.model.Notes

interface IPaginationService {
    suspend fun paginateNotes(page: Int, pageSize: Int, totalCount: Long, noteEntities: List<NoteEntity>): Notes
}