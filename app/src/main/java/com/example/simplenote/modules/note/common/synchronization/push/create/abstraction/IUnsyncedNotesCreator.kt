package com.example.simplenote.modules.note.common.synchronization.push.create.abstraction

import android.content.Context
import com.example.simplenote.core.exception.ExceptionIncludedResponse

interface IUnsyncedNotesCreator {
    suspend fun createUnsyncedNotesInServer(context: Context): ExceptionIncludedResponse?
}