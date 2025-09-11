package com.example.simplenote.modules.authentication.user.getuser.abstraction

import android.content.Context
import com.example.simplenote.modules.authentication.user.getuser.dto.response.GetUserResponse

interface IUserGetter {
    suspend fun getUser(context: Context): GetUserResponse
}