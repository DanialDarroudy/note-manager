package com.example.simplenote.modules.note.common.synchronization.push.delete.abstraction

import android.content.Context
import com.example.simplenote.core.exception.ExceptionIncludedResponse

interface IUnsyncedNotesDeleter {
    suspend fun deleteUnsyncedNotesFromServer(context: Context): ExceptionIncludedResponse?
}