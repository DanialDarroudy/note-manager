package com.example.simplenote.modules.note.common.synchronization.push.delete.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.synchronization.push.delete.abstraction.IUnsyncedNotesDeleter
import kotlinx.serialization.json.Json

class UnsyncedNotesDeleter: IUnsyncedNotesDeleter {
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun deleteUnsyncedNotesFromServer(context: Context): ExceptionIncludedResponse? {
        val unsyncedDeletedNoteEntities = noteDataAccessObject.getDeletedNotes()
        var indexOfDeletedNotes = 0
        while (indexOfDeletedNotes < unsyncedDeletedNoteEntities.size) {
            val noteEntity = unsyncedDeletedNoteEntities[indexOfDeletedNotes]
            val deleteResponse = simpleNoteApi.destroyNote(noteEntity.id)
            when {
                deleteResponse.code() == 401 -> {
                    val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
                    if (renewResponse is RenewTokenResponse.Failure) {
                        return renewResponse.exception
                    }
                    continue
                }
                !deleteResponse.isSuccessful -> {
                    return Json.Default.decodeFromString<ExceptionIncludedResponse>(deleteResponse.errorBody()?.string()!!)
                }
                else -> {
                    indexOfDeletedNotes++
                }
            }
        }
        return null
    }
}