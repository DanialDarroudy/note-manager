package com.example.simplenote.modules.authentication.token.renewtoken.abstraction

import android.content.Context
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse

interface ITokenRenewer {
    suspend fun renewToken(renewTokenRequestDto: RenewTokenRequestDto, context: Context): RenewTokenResponse
}