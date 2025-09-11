package com.example.simplenote.modules.note.getlist.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.pagination.abstraction.IPaginationService
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.synchronization.master.abstraction.IDatabaseSynchronizer
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.model.Notes
import com.example.simplenote.modules.note.common.model.NotesResponse
import com.example.simplenote.modules.note.getlist.abstraction.INoteListGetter
import com.example.simplenote.modules.note.getlist.dto.request.GetNoteListRequestDto
import kotlinx.serialization.json.Json

class NoteListGetter : INoteListGetter {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val paginationService: IPaginationService = DependencyProvider.paginationService
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer
    private val databaseSynchronizer: IDatabaseSynchronizer = DependencyProvider.databaseSynchronizer


    override suspend fun getNoteList(getNoteListRequestDto: GetNoteListRequestDto, context: Context): NotesResponse {
        return if (networkService.userIsOnline(context)) {
            val exception = databaseSynchronizer.synchronizeDatabase(context)
            if (exception == null){
                getNoteListOnServer(getNoteListRequestDto, context)
            }
            else {
                NotesResponse.Failure(exception)
            }
        } else {
            NotesResponse.Success(getNoteListLocally(getNoteListRequestDto))
        }
    }

    private suspend fun getNoteListOnServer(getNoteListRequestDto: GetNoteListRequestDto, context: Context): NotesResponse {
        val response = simpleNoteApi.getNoteList(getNoteListRequestDto.page, getNoteListRequestDto.pageSize)
        if (response.isSuccessful) {
            val notes = response.body()!!
            return NotesResponse.Success(notes)
        }
        else if (response.code() == 401){
            return refreshToken(getNoteListRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            // notes is definitely on the server.
            return NotesResponse.Failure(exception)
        }
    }

    private suspend fun getNoteListLocally(getNoteListRequestDto: GetNoteListRequestDto): Notes {
        val page = getNoteListRequestDto.page
        val pageSize = getNoteListRequestDto.pageSize
        val offset = (page - 1) * pageSize
        val noteEntities = noteDataAccessObject.getTotalNotes(limit = pageSize, offset)
        val totalCount = noteDataAccessObject.getTotalNotesCount()
        return paginationService.paginateNotes(page, pageSize, totalCount, noteEntities)
    }
    private suspend fun refreshToken(getNoteListRequestDto: GetNoteListRequestDto, context: Context): NotesResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                getNoteListOnServer(getNoteListRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NotesResponse.Failure(renewResponse.exception)
            }
        }
    }

}