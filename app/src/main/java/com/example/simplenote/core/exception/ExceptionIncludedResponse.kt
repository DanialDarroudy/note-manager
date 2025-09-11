package com.example.simplenote.core.exception

import kotlinx.serialization.Serializable

@Serializable
data class ExceptionIncludedResponse(
    val type: String?,
    val errors: List<ErrorResponse>
) {
    companion object {
        fun networkError(): ExceptionIncludedResponse {
            return ExceptionIncludedResponse(type = "NetworkError", errors = listOf(ErrorResponse("ConnectionError", "503", "Your internet access is down.")))
        }
    }
}
