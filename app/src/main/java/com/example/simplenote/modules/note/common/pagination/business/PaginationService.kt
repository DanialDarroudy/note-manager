package com.example.simplenote.modules.note.common.pagination.business

import com.example.simplenote.modules.note.common.pagination.abstraction.IPaginationService
import com.example.simplenote.modules.note.common.database.entity.NoteEntity
import com.example.simplenote.modules.note.common.extension.toNote
import com.example.simplenote.modules.note.common.model.Notes
import kotlin.math.ceil

class PaginationService: IPaginationService {

    override suspend fun paginateNotes(page: Int, pageSize: Int, totalCount: Long, noteEntities: List<NoteEntity>): Notes {
        val totalPages = ceil(totalCount.toDouble() / pageSize).toInt()
        val hasNext = page < totalPages
        val hasPrevious = page > 1
        return Notes(
            count = totalCount,
            nextPage = if (hasNext) "http://localhost:8000/api/notes/?page=${page + 1}&page_size=${pageSize}" else null,
            previousPage = if (hasPrevious) "http://localhost:8000/api/notes/?page=${page - 1}&page_size=${pageSize}" else null,
            notes = noteEntities.map { it.toNote() }
        )
    }
}