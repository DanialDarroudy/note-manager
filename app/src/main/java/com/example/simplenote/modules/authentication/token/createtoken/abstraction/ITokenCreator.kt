package com.example.simplenote.modules.authentication.token.createtoken.abstraction

import android.content.Context
import com.example.simplenote.modules.authentication.token.createtoken.dto.request.CreateTokenRequestDto
import com.example.simplenote.modules.authentication.token.createtoken.dto.response.CreateTokenResponse

interface ITokenCreator {
    suspend fun createToken(createTokenRequestDto: CreateTokenRequestDto, context: Context): CreateTokenResponse
}