package com.example.simplenote.modules.authentication.user.createuser.dto.request

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUserRequestDto(
    @SerializedName("username") val userName: String,
    @SerializedName("password") val passWord: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String
)
