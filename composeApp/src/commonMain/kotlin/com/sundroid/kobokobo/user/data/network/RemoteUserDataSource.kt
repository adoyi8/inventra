package com.sundroid.kobokobo.user.data.network

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.user.data.dto.UserLoginResponseDto
import com.sundroid.kobokobo.user.data.dto.UserResponseDto

interface RemoteUserDataSource {
    suspend fun searchUser(
        query: String,
        resultLimit: Int? = null
    ): Result<UserResponseDto, DataError.Remote>

    suspend fun login(username: String, password: String): Result<UserLoginResponseDto, DataError.Remote>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<ResponseDto, DataError.Remote>
}