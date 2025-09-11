package com.example.simplenote.modules.note.retrieve.business

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
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.retrieve.abstraction.INoteRetriever
import com.example.simplenote.modules.note.retrieve.dto.request.RetrieveNoteRequestDto
import kotlinx.serialization.json.Json

class NoteRetriever: INoteRetriever {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer


    override suspend fun retrieveNote(retrieveNoteRequestDto: RetrieveNoteRequestDto, context: Context): NoteResponse {
        return if (networkService.userIsOnline(context)) {
            retrieveNoteOnServer(retrieveNoteRequestDto, context)
        } else {
            NoteResponse.Success(retrieveNoteLocally(retrieveNoteRequestDto))
        }
    }

    private suspend fun retrieveNoteOnServer(retrieveNoteRequestDto: RetrieveNoteRequestDto, context: Context): NoteResponse {
        val response = simpleNoteApi.retrieveNote(retrieveNoteRequestDto.id)
        if (response.isSuccessful){
            val note = response.body()!!
            return NoteResponse.Success(note)
        }
        else if (response.code() == 401){
            return refreshToken(retrieveNoteRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            // note is definitely on the server.
            return NoteResponse.Failure(exception)
        }
    }

    private suspend fun retrieveNoteLocally(retrieveNoteRequestDto: RetrieveNoteRequestDto): Note {
        val noteEntity = noteDataAccessObject.getNoteById(retrieveNoteRequestDto.id)!!
        return noteEntity.toNote()
    }
    private suspend fun refreshToken(retrieveNoteRequestDto: RetrieveNoteRequestDto, context: Context): NoteResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                retrieveNoteOnServer(retrieveNoteRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NoteResponse.Failure(renewResponse.exception)
            }
        }
    }

}