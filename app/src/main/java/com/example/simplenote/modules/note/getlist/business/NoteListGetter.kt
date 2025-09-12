package com.example.simplenote.modules.note.getlist.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.extension.toNote
import com.example.simplenote.modules.note.common.synchronization.master.abstraction.IDatabaseSynchronizer
import com.example.simplenote.modules.note.getlist.abstraction.INoteListGetter
import com.example.simplenote.modules.note.getlist.dto.response.GetNoteListResponse
import com.example.simplenote.modules.note.getlist.dto.response.GetNoteListResponseDto
import kotlinx.serialization.json.Json

class NoteListGetter : INoteListGetter {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer
    private val databaseSynchronizer: IDatabaseSynchronizer = DependencyProvider.databaseSynchronizer


    override suspend fun getNoteList(context: Context): GetNoteListResponse {
        return if (networkService.userIsOnline(context)) {
            val exception = databaseSynchronizer.synchronizeDatabase(context)
            if (exception == null){
                getNoteListOnServer(context)
            }
            else {
                GetNoteListResponse.Failure(exception)
            }
        }
        else {
            GetNoteListResponse.Success(getNoteListLocally())
        }
    }

    private suspend fun getNoteListOnServer(context: Context): GetNoteListResponse {
        val response = simpleNoteApi.getNoteList(null, null)
        if (response.isSuccessful) {
            val notes = response.body()!!
            return GetNoteListResponse.Success(GetNoteListResponseDto(notes.notes))
        }
        else if (response.code() == 401){
            return refreshToken(context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            // notes is definitely on the server.
            return GetNoteListResponse.Failure(exception)
        }
    }

    private suspend fun getNoteListLocally(): GetNoteListResponseDto {
        val noteEntities = noteDataAccessObject.getTotalNotes()
        return GetNoteListResponseDto(noteEntities.map { it.toNote() })
    }
    private suspend fun refreshToken(context: Context): GetNoteListResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                getNoteListOnServer(context)
            }
            is RenewTokenResponse.Failure -> {
                GetNoteListResponse.Failure(renewResponse.exception)
            }
        }
    }

}