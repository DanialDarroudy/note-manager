package com.example.simplenote.modules.note.getfilteredlist.business

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
import com.example.simplenote.modules.note.common.model.Notes
import com.example.simplenote.modules.note.common.model.NotesResponse
import com.example.simplenote.modules.note.common.pagination.abstraction.IPaginationService
import com.example.simplenote.modules.note.getfilteredlist.abstraction.IFilteredNoteListGetter
import com.example.simplenote.modules.note.getfilteredlist.dto.request.GetFilteredNoteListRequestDto
import kotlinx.serialization.json.Json

class FilteredNoteListGetter: IFilteredNoteListGetter {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val paginationService: IPaginationService = DependencyProvider.paginationService
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer


    override suspend fun getFilteredNoteList(getFilteredNoteListRequestDto: GetFilteredNoteListRequestDto, context: Context): NotesResponse {
        return if (networkService.userIsOnline(context)) {
            getFilteredNoteListOnServer(getFilteredNoteListRequestDto, context)
        } else {
            NotesResponse.Success(getFilteredNoteListLocally(getFilteredNoteListRequestDto))
        }

    }

    private suspend fun getFilteredNoteListOnServer(getFilteredNoteListRequestDto: GetFilteredNoteListRequestDto, context: Context): NotesResponse {
        val response = simpleNoteApi.getFilteredNoteList(
            page = getFilteredNoteListRequestDto.page,
            pageSize = getFilteredNoteListRequestDto.pageSize,
            title = getFilteredNoteListRequestDto.title,
            description = getFilteredNoteListRequestDto.description
        )

        if (response.isSuccessful) {
            val notes = response.body()!!
            return NotesResponse.Success(notes)
        }
        else if (response.code() == 401){
            return refreshToken(getFilteredNoteListRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            // notes is definitely on the server.
            return NotesResponse.Failure(exception)
        }
    }

    private suspend fun getFilteredNoteListLocally(getFilteredNoteListRequestDto: GetFilteredNoteListRequestDto): Notes {
        val page = getFilteredNoteListRequestDto.page
        val pageSize = getFilteredNoteListRequestDto.pageSize
        val title = getFilteredNoteListRequestDto.title
        val description = getFilteredNoteListRequestDto.description
        val offset = (page - 1) * pageSize

        val noteEntities = noteDataAccessObject.getFilteredNotes(
            title = title,
            description = description,
            limit = pageSize,
            offset = offset
        )

        val totalCount = noteDataAccessObject.getFilteredNotesCount(
            title = title,
            description = description
        )
        return paginationService.paginateNotes(page, pageSize, totalCount, noteEntities)
    }
    private suspend fun refreshToken(getFilteredNoteListRequestDto: GetFilteredNoteListRequestDto, context: Context): NotesResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                getFilteredNoteListOnServer(getFilteredNoteListRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NotesResponse.Failure(renewResponse.exception)
            }
        }
    }

}