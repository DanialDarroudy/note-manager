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
    @Query("SELECT * FROM notes WHERE isDeleted = 0 ORDER BY createdAt DESC LIMIT :limit OFFSET :offset")
    suspend fun getTotalNotes(limit: Int, offset: Int): List<NoteEntity>
    @Query("SELECT COUNT(*) FROM notes WHERE isDeleted = 0")
    suspend fun getTotalNotesCount(): Long
    @Query("""
        SELECT * FROM notes 
        WHERE isDeleted = 0 
        AND (:title IS NULL OR title LIKE '%' || :title || '%')
        AND (:description IS NULL OR description LIKE '%' || :description || '%')
        ORDER BY createdAt DESC 
        LIMIT :limit OFFSET :offset
    """)
    suspend fun getFilteredNotes(title: String?, description: String?, limit: Int, offset: Int): List<NoteEntity>
    @Query("""
        SELECT COUNT(*) FROM notes 
        WHERE isDeleted = 0 
        AND (:title IS NULL OR title LIKE '%' || :title || '%')
        AND (:description IS NULL OR description LIKE '%' || :description || '%')
    """)
    suspend fun getFilteredNotesCount(title: String?, description: String?): Long

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