package com.example.simplenote.modules.authentication.token.createtoken.business

import android.content.Context
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.modules.authentication.common.api.IAuthenticationApi
import com.example.simplenote.modules.authentication.token.createtoken.abstraction.ITokenCreator
import com.example.simplenote.modules.authentication.token.createtoken.dto.request.CreateTokenRequestDto
import com.example.simplenote.modules.authentication.token.createtoken.dto.response.CreateTokenResponse
import kotlinx.serialization.json.Json

class TokenCreator: ITokenCreator {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val authenticationApi: IAuthenticationApi = DependencyProvider.authenticationApi

    override suspend fun createToken(createTokenRequestDto: CreateTokenRequestDto, context: Context): CreateTokenResponse {
        if (networkService.userIsOnline(context)) {
            val response = authenticationApi.createToken(createTokenRequestDto)
            if (response.isSuccessful){
                val createTokenResponseDto = response.body()!!
                ConstantProvider.refreshToken = createTokenResponseDto.refresh
                ConstantProvider.accessToken = createTokenResponseDto.access
                return CreateTokenResponse.Success(createTokenResponseDto)
            }
            else {
                val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
                return CreateTokenResponse.Failure(exception)
            }
        } else {
            return CreateTokenResponse.Failure(ExceptionIncludedResponse.networkError())
        }
    }
}