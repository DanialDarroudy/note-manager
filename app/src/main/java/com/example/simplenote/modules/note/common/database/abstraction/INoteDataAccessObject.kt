package com.example.simplenote.modules.note.common.database.abstraction

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.simplenote.modules.note.common.database.entity.NoteEntity

@Dao
interface INoteDataAccessObject {
    @Insert
    suspend fun insert(note: NoteEntity)
    @Query("UPDATE notes SET isDeleted = 1 WHERE id = :id")
    suspend fun markAsDeleted(id: Long)
    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteById(id: Long)
    @Update
    suspend fun update(note: NoteEntity)
    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Long): NoteEntity?
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC")
    suspend fun getTotalNotes(): List<NoteEntity>
    @Insert
    suspend fun insertAll(notes: List<NoteEntity>)
    @Query("DELETE FROM notes")
    suspend fun deleteAllNotes()
    @Query("SELECT * FROM notes WHERE isCreated = 1")
    suspend fun getCreatedNotes(): List<NoteEntity>
    @Query("SELECT * FROM notes WHERE isUpdated = 1 AND isDeleted = 0")
    suspend fun getUpdatedNotes(): List<NoteEntity>
    @Query("SELECT * FROM notes WHERE isDeleted = 1")
    suspend fun getDeletedNotes(): List<NoteEntity>
}