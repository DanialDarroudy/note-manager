package com.example.simplenote.modules.note.create.business

import android.content.Context
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.common.database.abstraction.INoteDataAccessObject
import com.example.simplenote.modules.note.common.database.entity.NoteEntity
import com.example.simplenote.modules.note.common.model.Note
import com.example.simplenote.modules.note.common.model.NoteResponse
import com.example.simplenote.modules.note.create.abstraction.INoteCreator
import com.example.simplenote.modules.note.create.dto.request.CreateNoteRequestDto
import com.example.simplenote.modules.note.common.extension.toNote
import com.example.simplenote.modules.note.common.extension.toNoteEntity
import kotlinx.serialization.json.Json
import java.util.Date
import kotlin.random.Random

class NoteCreator : INoteCreator {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val noteDataAccessObject: INoteDataAccessObject = DependencyProvider.noteDataAccessObject
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun createNote(createNoteRequestDto: CreateNoteRequestDto, context: Context): NoteResponse {
        return if (networkService.userIsOnline(context)) {
            createNoteOnServer(createNoteRequestDto, context)
        } else {
            NoteResponse.Success(createNoteLocally(createNoteRequestDto))
        }
    }

    private suspend fun createNoteOnServer(createNoteRequestDto: CreateNoteRequestDto, context: Context): NoteResponse {
        val response = simpleNoteApi.createNote(createNoteRequestDto)
        if (response.isSuccessful){
            val note = response.body()!!
            createNoteLocally(note)
            return NoteResponse.Success(note)
        }
        else if (response.code() == 401){
            return refreshToken(createNoteRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            return NoteResponse.Failure(exception)
        }
    }

    private suspend fun createNoteLocally(createNoteRequestDto: CreateNoteRequestDto): Note {
        val id = Random.nextLong(1000, 20000)
        val noteEntity = NoteEntity(
            id,
            createNoteRequestDto.title,
            createNoteRequestDto.description,
            Date().toString(),
            Date().toString(),
            isCreated = true,
            isUpdated = false,
            isDeleted = false
        )
        noteDataAccessObject.insert(noteEntity)
        return noteEntity.toNote()
    }
    private suspend fun createNoteLocally(note: Note) {
        val noteEntity = note.toNoteEntity()
        noteDataAccessObject.insert(noteEntity)
    }

    private suspend fun refreshToken(createNoteRequestDto: CreateNoteRequestDto, context: Context): NoteResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                createNoteOnServer(createNoteRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                NoteResponse.Failure(renewResponse.exception)
            }
        }
    }
}