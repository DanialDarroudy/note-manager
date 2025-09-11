package com.example.simplenote.modules.authentication.token.createtoken.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateTokenRequestDto(
    @SerializedName("username") val userName: String,
    @SerializedName("password") val passWord: String
)
