package com.example.simplenote.modules.note.createbulk.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.note.common.api.ISimpleNoteApi
import com.example.simplenote.modules.note.createbulk.abstraction.IBulkNoteCreator
import com.example.simplenote.modules.note.createbulk.dto.request.CreateBulkNoteRequestDto
import com.example.simplenote.modules.note.createbulk.dto.response.CreateBulkNoteResponse
import com.example.simplenote.modules.note.createbulk.dto.response.CreateBulkNoteResponseDto
import kotlinx.serialization.json.Json

class BulkNoteCreator: IBulkNoteCreator {
    private val simpleNoteApi: ISimpleNoteApi = DependencyProvider.simpleNoteApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer

    override suspend fun createBulkNote(createBulkNoteRequestDto: CreateBulkNoteRequestDto, context: Context): CreateBulkNoteResponse {
        val response = simpleNoteApi.createBulkNote(createBulkNoteRequestDto.requests)
        if (response.isSuccessful){
            val responseDto = response.body()!!
            return CreateBulkNoteResponse.Success(CreateBulkNoteResponseDto(responseDto))
        }
        else if (response.code() == 401){
            return refreshToken(createBulkNoteRequestDto, context)
        }
        else {
            val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
            return CreateBulkNoteResponse.Failure(exception)
        }
    }
    private suspend fun refreshToken(createBulkNoteRequestDto: CreateBulkNoteRequestDto, context: Context): CreateBulkNoteResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                createBulkNote(createBulkNoteRequestDto, context)
            }
            is RenewTokenResponse.Failure -> {
                CreateBulkNoteResponse.Failure(renewResponse.exception)
            }
        }
    }
}