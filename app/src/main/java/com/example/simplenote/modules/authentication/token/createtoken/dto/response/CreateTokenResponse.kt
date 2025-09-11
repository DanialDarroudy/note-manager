package com.example.simplenote.modules.authentication.token.createtoken.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class CreateTokenResponse {
    data class Success(val createTokenResponseDto: CreateTokenResponseDto) : CreateTokenResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : CreateTokenResponse()
}