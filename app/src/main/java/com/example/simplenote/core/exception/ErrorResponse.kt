package com.example.simplenote.core.exception

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ErrorResponse(
    @SerialName("attr") val attribute: String?,
    val code: String?,
    val detail: String?
)
