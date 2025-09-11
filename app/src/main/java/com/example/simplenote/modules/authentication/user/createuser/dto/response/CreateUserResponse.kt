package com.example.simplenote.modules.authentication.user.createuser.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class CreateUserResponse {
    data class Success(val createUserResponseDto: CreateUserResponseDto) : CreateUserResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : CreateUserResponse()
}