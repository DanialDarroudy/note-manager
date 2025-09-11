package com.example.simplenote.modules.note.common.database.entity

import androidx.room.*
import kotlinx.serialization.Serializable

@Serializable
@Entity(tableName = "notes")
data class NoteEntity(
    @PrimaryKey(autoGenerate = false)
    val id: Long,
    var title: String,
    var description: String,
    val createdAt: String,
    var updatedAt: String,
    var isCreated: Boolean,
    var isUpdated: Boolean,
    val isDeleted: Boolean,
)