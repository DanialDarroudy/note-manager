package com.example.simplenote.modules.authentication.token.renewtoken.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class RenewTokenResponse {
    data class Success(val renewTokenResponseDto: RenewTokenResponseDto) : RenewTokenResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : RenewTokenResponse()
}