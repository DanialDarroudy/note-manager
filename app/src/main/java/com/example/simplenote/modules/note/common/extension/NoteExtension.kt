package com.example.simplenote.modules.note.common.extension

import com.example.simplenote.modules.note.common.database.entity.NoteEntity
import com.example.simplenote.modules.note.common.model.Note

fun NoteEntity.toNote(): Note {
    return Note(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        creatorName = null,
        creatorUserName = null
    )
}

fun Note.toNoteEntity(): NoteEntity {
    return NoteEntity(
        id = id,
        title = title,
        description = description,
        createdAt = createdAt,
        updatedAt = updatedAt,
        isCreated = false,
        isUpdated = false,
        isDeleted = false
    )
}