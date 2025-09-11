package com.example.simplenote.modules.authentication.user.createuser.business

import android.content.Context
import com.example.simplenote.core.dependencyinjection.DependencyProvider
import com.example.simplenote.core.exception.ExceptionIncludedResponse
import com.example.simplenote.core.network.abstraction.INetworkService
import com.example.simplenote.modules.authentication.common.api.IAuthenticationApi
import com.example.simplenote.modules.authentication.user.createuser.abstraction.IUserCreator
import com.example.simplenote.modules.authentication.user.createuser.dto.request.CreateUserRequestDto
import com.example.simplenote.modules.authentication.user.createuser.dto.response.CreateUserResponse
import kotlinx.serialization.json.Json

class UserCreator: IUserCreator {
    private val networkService: INetworkService = DependencyProvider.networkService
    private val authenticationApi: IAuthenticationApi = DependencyProvider.authenticationApi

    override suspend fun createUser(createUserRequestDto: CreateUserRequestDto, context: Context): CreateUserResponse {
        if (networkService.userIsOnline(context)) {
            val response = authenticationApi.createUser(createUserRequestDto)
            if (response.isSuccessful){
                val createUserResponseDto = response.body()!!
                return CreateUserResponse.Success(createUserResponseDto)
            }
            else {
                val exception = Json.decodeFromString<ExceptionIncludedResponse>(response.errorBody()?.string()!!)
                return CreateUserResponse.Failure(exception)
            }
        } else {
            return CreateUserResponse.Failure(ExceptionIncludedResponse.networkError())
        }
    }
}