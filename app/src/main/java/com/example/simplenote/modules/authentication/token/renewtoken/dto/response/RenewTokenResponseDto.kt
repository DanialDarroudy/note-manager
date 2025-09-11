package com.example.simplenote.modules.authentication.token.renewtoken.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class RenewTokenResponseDto(
    val access: String
)
