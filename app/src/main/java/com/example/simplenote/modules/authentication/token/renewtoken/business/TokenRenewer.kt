package com.example.simplenote.modules.authentication.token.renewtoken.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.common.api.IAuthenticationApi
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import kotlinx.serialization.json.Json

class TokenRenewer: ITokenRenewer {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val authenticationApi: IAuthenticationApi = DependencyProvider.authenticationApi

    override suspend fun renewToken(renewTokenRequestDto: RenewTokenRequestDto, context: Context): RenewTokenResponse {
        if (networkService.userIsOnline(context)) {
            val response = authenticationApi.renewToken(renewTokenRequestDto)
            if (response.isSuccessful){
                val renewTokenResponseDto = response.body()!!
                ConstantProvider.accessToken = renewTokenResponseDto.access
                return RenewTokenResponse.Success(renewTokenResponseDto)
            }
            else {
                val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
                return RenewTokenResponse.Failure(exception)
            }
        } else {
            return RenewTokenResponse.Failure(ExceptionIncludedResponse.networkError())
        }
    }
}