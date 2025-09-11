package com.example.simplenote.modules.note.common.synchronization.master.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.modules.note.common.synchronization.master.abstraction.IDatabaseSynchronizer
import com.example.simplenote.modules.note.common.synchronization.pull.abstraction.IPuller
import com.example.simplenote.modules.note.common.synchronization.push.master.abstraction.IPusher

class DatabaseSynchronizer: IDatabaseSynchronizer {
    private val pusher: IPusher = DependencyProvider.pusher
    private val puller: IPuller = DependencyProvider.puller

    override suspend fun synchronizeDatabase(context: Context): ExceptionIncludedResponse? {
        val pushException = pusher.pushUnsyncedNotesToServer(context)
        if (pushException != null){
            return pushException
        }

        val pullException = puller.pullAllNotesFromServer(context)
        if (pullException != null){
            return pullException
        }
        return null
    }
}