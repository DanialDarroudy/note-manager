package com.example.simplenote.modules.authentication.user.getuser.dto.response

import com.example.simplenote.core.exception.ExceptionIncludedResponse

sealed class GetUserResponse {
    data class Success(val getUserResponseDto: GetUserResponseDto) : GetUserResponse()
    data class Failure(val exception: ExceptionIncludedResponse) : GetUserResponse()
}