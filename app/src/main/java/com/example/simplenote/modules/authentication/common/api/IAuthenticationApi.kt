package com.example.simplenote.modules.authentication.common.api

import com.example.simplenote.modules.authentication.token.createtoken.dto.request.CreateTokenRequestDto
import com.example.simplenote.modules.authentication.token.createtoken.dto.response.CreateTokenResponseDto
import com.example.simplenote.modules.authentication.user.createuser.dto.request.CreateUserRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.request.RenewTokenRequestDto
import com.example.simplenote.modules.authentication.token.renewtoken.dto.response.RenewTokenResponseDto
import com.example.simplenote.modules.authentication.user.createuser.dto.response.CreateUserResponseDto
import com.example.simplenote.modules.authentication.user.getuser.dto.response.GetUserResponseDto
import retrofit2.Response
import retrofit2.http.*

interface IAuthenticationApi {
    @POST("/api/auth/register/")
    suspend fun createUser(@Body createUserRequestDto: CreateUserRequestDto): Response<CreateUserResponseDto>

    @POST("/api/auth/token/")
    suspend fun createToken(@Body createTokenRequestDto: CreateTokenRequestDto): Response<CreateTokenResponseDto>

    @POST("/api/auth/token/refresh/")
    suspend fun renewToken(@Body renewTokenRequestDto: RenewTokenRequestDto): Response<RenewTokenResponseDto>

    @GET("/api/auth/userinfo/")
    suspend fun getUser(): Response<GetUserResponseDto>
}