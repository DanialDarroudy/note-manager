package com.example.simplenote.modules.authentication.user.getuser.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.core.network.constant.ConstantProvider
import com.example.simplenote.modules.authentication.common.api.IAuthenticationApi
import com.example.simplenote.modules.authentication.token.renewtoken.abstraction.ITokenRenewer
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponse
import com.example.simplenote.modules.authentication.user.getuser.abstraction.IUserGetter
import com.example.simplenote.modules.authentication.user.getuser.dto.response.GetUserResponse
import com.example.simplenote.modules.authentication.user.getuser.dto.response.GetUserResponseDto
import com.example.simplenote.modules.note.common.model.NoteResponse
import kotlinx.serialization.json.Json

class UserGetter: IUserGetter {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val authenticationApi: IAuthenticationApi = DependencyProvider.authenticationApi
    private val tokenRenewer: ITokenRenewer = DependencyProvider.tokenRenewer


    override suspend fun getUser(context: Context): GetUserResponse {
        if (networkService.userIsOnline(context)) {
            val response = authenticationApi.getUser()
            if (response.isSuccessful){
                val getUserResponseDto = response.body()!!
                return GetUserResponse.Success(getUserResponseDto)
            }
            else if (response.code() == 401){
                return refreshToken(context)
            }
            else {
                val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
                return GetUserResponse.Failure(exception)
            }
        } else {
            return GetUserResponse.Failure(ExceptionIncludedResponse.networkError())
        }
    }

    private suspend fun refreshToken(context: Context): GetUserResponse {
        val renewResponse = tokenRenewer.renewToken(RenewTokenRequestDto(ConstantProvider.refreshToken), context)
        return when (renewResponse) {
            is RenewTokenResponse.Success -> {
                getUser(context)
            }
            is RenewTokenResponse.Failure -> {
                GetUserResponse.Failure(renewResponse.exception)
            }
        }
    }
}