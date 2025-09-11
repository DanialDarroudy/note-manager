package com.example.simplenote.modules.note.common.synchronization.pull.abstraction

import android.content.Context
import com.example.simplenote.core.exception.ExceptionIncludedResponse

interface IPuller {
    suspend fun pullAllNotesFromServer(context: Context): ExceptionIncludedResponse?
}