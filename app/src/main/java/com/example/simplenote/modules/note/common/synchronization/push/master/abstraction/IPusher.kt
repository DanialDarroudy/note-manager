package com.example.simplenote.modules.note.common.synchronization.push.master.abstraction

import android.content.Context
import com.example.simplenote.core.exception.ExceptionIncludedResponse

interface IPusher {
    suspend fun pushUnsyncedNotesToServer(context: Context): ExceptionIncludedResponse?
}