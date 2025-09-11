package com.example.simplenote.modules.note.destroy.business

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
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.destroy.abstraction.INoteDestroyer
import com.example.simplenote.modules.note.destroy.dto.request.DestroyNoteRequestDto
import kotlinx.serialization.json.Json

class NoteDestroyer: INoteDestroyer {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun destroyNote(destroyNoteRequestDto: DestroyNoteRequestDto, context: Context): NoteResponse {
        return if (networkService.userIsOnline(context)) {
            destroyNoteOnServer(destroyNoteRequestDto, context)
        } else {
            destroyNoteLocally(destroyNoteRequestDto)
            NoteResponse.Success(null)
        }
    }

    private suspend fun destroyNoteOnServer(destroyNoteRequestDto: DestroyNoteRequestDto, context: Context): NoteResponse {
        val id = destroyNoteRequestDto.id
        val response = simpleNoteApi.destroyNote(id)
        if (response.isSuccessful){
            destroyNoteLocally(id)
            return NoteResponse.Success(null)
        }
        else if (response.code() == 401){
            return refreshToken(destroyNoteRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            return NoteResponse.Failure(exception)
        }
    }

    private suspend fun destroyNoteLocally(destroyNoteRequestDto: DestroyNoteRequestDto) {
        val id = destroyNoteRequestDto.id
        val noteEntity = noteDataAccessObject.getNoteById(id)!!
        if (noteEntity.isCreated){
            noteDataAccessObject.deleteById(id)
        }
        else {
            noteDataAccessObject.markAsDeleted(destroyNoteRequestDto.id)
        }
    }
    private suspend fun destroyNoteLocally(id: Long) {
        noteDataAccessObject.deleteById(id)
    }
    private suspend fun refreshToken(destroyNoteRequestDto: DestroyNoteRequestDto, context: Context): NoteResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                destroyNoteOnServer(destroyNoteRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NoteResponse.Failure(renewResponse.exception)
            }
        }
    }

}