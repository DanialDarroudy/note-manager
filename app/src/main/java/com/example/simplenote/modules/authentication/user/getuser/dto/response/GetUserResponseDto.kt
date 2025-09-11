package com.example.simplenote.modules.authentication.user.getuser.dto.response

import com.google.gson.annotations.SerializedName
import kotlinx.serialization.Serializable

@Serializable
data class GetUserResponseDto(
    val id: Long,
    val userName: String,
    val email: String,
    @SerializedName("first_name") val firstName: String,
    @SerializedName("last_name") val lastName: String

)
