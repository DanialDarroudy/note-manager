package com.example.simplenote.modules.authentication.user.createuser.abstraction

import android.content.Context
import com.example.simplenote.modules.authentication.user.createuser.dto.request.CreateUserRequestDto
import com.example.simplenote.modules.authentication.user.createuser.dto.response.CreateUserResponse

interface IUserCreator {
    suspend fun createUser(createUserRequestDto: CreateUserRequestDto, context: Context): CreateUserResponse
}