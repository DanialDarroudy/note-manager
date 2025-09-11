package com.example.simplenote.core.database.business

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.database.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    abstract fun noteDataAccessObject(): INoteDataAccessObject
}