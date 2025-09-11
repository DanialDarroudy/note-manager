package com.example.simplenote.modules.note.common.synchronization.push.update.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.synchronization.push.update.abstraction.IUnsyncedNotesUpdater
import com.example.simplenote.modules.note.update.dto.request.UpdateNoteRequestDto
import kotlinx.serialization.json.Json

class UnsyncedNotesUpdater: IUnsyncedNotesUpdater {
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun updateUnsyncedNotesInServer(context: Context): ExceptionIncludedResponse? {
        val unsyncedUpdatedNoteEntities = noteDataAccessObject.getUpdatedNotes()
        var indexOfUpdatedNotes = 0
        while (indexOfUpdatedNotes < unsyncedUpdatedNoteEntities.size) {
            val noteEntity = unsyncedUpdatedNoteEntities[indexOfUpdatedNotes]
            val updateNoteRequestDto = UpdateNoteRequestDto(noteEntity.id, noteEntity.title, noteEntity.description)
            val updateResponse = simpleNoteApi.updateNote(noteEntity.id, updateNoteRequestDto)
            when {
                updateResponse.code() == 401 -> {
                    val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
                    if (renewResponse is RenewTokenResponse.Failure) {
                        return renewResponse.exception
                    }
                    continue
                }
                !updateResponse.isSuccessful -> {
                    return Json.Default.decodeFromString<ExceptionIncludedResponse>(updateResponse.errorBody()?.string()!!)
                }
                else -> {
                    indexOfUpdatedNotes++
                }
            }
        }
        return null
    }
}