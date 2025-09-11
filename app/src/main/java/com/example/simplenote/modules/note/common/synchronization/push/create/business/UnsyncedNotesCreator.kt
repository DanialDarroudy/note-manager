package com.example.simplenote.modules.note.common.synchronization.push.create.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.synchronization.push.create.abstraction.IUnsyncedNotesCreator
import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto
import com.example.simplenote.modules.note.createbulk.abstraction.IBulkNoteCreator
import com.example.simplenote.modules.note.createbulk.dto.request.CreateBulkNoteRequestDto
import com.example.simplenote.modules.note.createbulk.dto.response.CreateBulkNoteResponse

class UnsyncedNotesCreator: IUnsyncedNotesCreator {
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val bulkNoteCreator: IBulkNoteCreator = DependencyProvider.bulkNoteCreator

    override suspend fun createUnsyncedNotesInServer(context: Context): ExceptionIncludedResponse? {
        val unsyncedDeletedNoteEntities = noteDataAccessObject.getCreatedNotes()
        if (unsyncedDeletedNoteEntities.isEmpty()) return null
        val requests = unsyncedDeletedNoteEntities.map { CreateNoteRequestDto(it.title, it.description) }
        val createBulkNoteRequestDto = CreateBulkNoteRequestDto(requests)
        val createResponse = bulkNoteCreator.createBulkNote(createBulkNoteRequestDto, context)
        if (createResponse is CreateBulkNoteResponse.Failure){
            return createResponse.exception
        }
        return null
    }
}