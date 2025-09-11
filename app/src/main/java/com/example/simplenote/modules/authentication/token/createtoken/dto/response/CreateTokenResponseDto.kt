package com.example.simplenote.modules.authentication.token.createtoken.dto.response

import kotlinx.serialization.Serializable

@Serializable
data class CreateTokenResponseDto(
    val access: String,
    val refresh: String
)
