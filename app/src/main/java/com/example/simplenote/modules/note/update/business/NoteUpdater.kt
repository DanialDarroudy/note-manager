package com.example.simplenote.modules.note.update.business

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
import com.example.simplenote.modules.note.common.extension.toNoteEntity
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.update.dto.request.UpdateNoteRequestDto
import com.example.simplenote.modules.note.update.abstraction.INoteUpdater
import kotlinx.serialization.json.Json
import java.util.Date

class NoteUpdater: INoteUpdater {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer


    override suspend fun updateNote(updateNoteRequestDto: UpdateNoteRequestDto, context: Context): NoteResponse {
        return if (networkService.userIsOnline(context)) {
            updateNoteOnServer(updateNoteRequestDto, context)
        } else {
            NoteResponse.Success(updateNoteLocally(updateNoteRequestDto))
        }
    }

    private suspend fun updateNoteOnServer(updateNoteRequestDto: UpdateNoteRequestDto, context: Context): NoteResponse {
        val response = simpleNoteApi.updateNote(updateNoteRequestDto.id, updateNoteRequestDto)
        if (response.isSuccessful){
            val note = response.body()!!
            updateNoteLocally(note)
            return NoteResponse.Success(note)
        }
        else if (response.code() == 401){
            return refreshToken(updateNoteRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            return NoteResponse.Failure(exception)
        }
    }
    private suspend fun updateNoteLocally(updateNoteRequestDto: UpdateNoteRequestDto): Note {
        val noteEntity = noteDataAccessObject.getNoteById(updateNoteRequestDto.id)!!
        noteEntity.title = updateNoteRequestDto.title
        noteEntity.description = updateNoteRequestDto.description
        noteEntity.updatedAt = Date().toString()
        if (!noteEntity.isCreated){
            noteEntity.isUpdated = true
        }
        noteDataAccessObject.update(noteEntity)
        return noteEntity.toNote()
    }
    private suspend fun updateNoteLocally(note: Note) {
        val noteEntity = note.toNoteEntity()
        noteDataAccessObject.update(noteEntity)
    }
    private suspend fun refreshToken(updateNoteRequestDto: UpdateNoteRequestDto, context: Context): NoteResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                updateNoteOnServer(updateNoteRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NoteResponse.Failure(renewResponse.exception)
            }
        }
    }

}