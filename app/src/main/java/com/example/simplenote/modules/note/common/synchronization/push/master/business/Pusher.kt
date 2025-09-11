package com.example.simplenote.modules.note.common.synchronization.push.master.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.modules.note.common.synchronization.push.create.abstraction.IUnsyncedNotesCreator
import com.example.simplenote.modules.note.common.synchronization.push.delete.abstraction.IUnsyncedNotesDeleter
import com.example.simplenote.modules.note.common.synchronization.push.master.abstraction.IPusher
import com.example.simplenote.modules.note.common.synchronization.push.update.abstraction.IUnsyncedNotesUpdater

class Pusher: IPusher {
    private val unsyncedNotesDeleter: IUnsyncedNotesDeleter = DependencyProvider.unsyncedNotesDeleter
    private val unsyncedNotesUpdater: IUnsyncedNotesUpdater = DependencyProvider.unsyncedNotesUpdater
    private val unsyncedNotesCreator: IUnsyncedNotesCreator = DependencyProvider.unsyncedNotesCreator

    override suspend fun pushUnsyncedNotesToServer(context: Context): ExceptionIncludedResponse? {
        val deleteException = unsyncedNotesDeleter.deleteUnsyncedNotesFromServer(context)
        if (deleteException != null){
            return deleteException
        }

        val updateException = unsyncedNotesUpdater.updateUnsyncedNotesInServer(context)
        if (updateException != null){
            return updateException
        }

        val createException = unsyncedNotesCreator.createUnsyncedNotesInServer(context)
        if (createException != null){
            return createException
        }
        return null
    }
}