package com.example.simplenote.modules.note.common.synchronization.pull.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.extension.toNoteEntity
import com.example.simplenote.modules.note.common.synchronization.pull.abstraction.IPuller
import kotlinx.serialization.json.Json

class Puller: IPuller {
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun pullAllNotesFromServer(context: Context): ExceptionIncludedResponse? {
        noteDataAccessObject.deleteAllNotes()
        val allNotesResponse = simpleNoteApi.getNoteList(null, null)
        if (allNotesResponse.code() == 401){
            val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
            if (renewResponse is RenewTokenResponse.Failure) {
                return renewResponse.exception
            }
            val allNotesResponse = simpleNoteApi.getNoteList(null, null)
            if (!allNotesResponse.isSuccessful){
                return Json.Default.decodeFromString<ExceptionIncludedResponse>(allNotesResponse.errorBody()?.string()!!)
            }
        }
        else if (!allNotesResponse.isSuccessful){
            return Json.Default.decodeFromString<ExceptionIncludedResponse>(allNotesResponse.errorBody()?.string()!!)
        }
        val allNotes = allNotesResponse.body()!!
        if (allNotes.notes.isEmpty()) return null
        val allNoteEntities = allNotes.notes.map { it.toNoteEntity() }
        noteDataAccessObject.insertAll(allNoteEntities)
        return null

    }
}