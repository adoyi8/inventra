package com.ikemba.inventrar.user.data.network

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.user.data.dto.UserLoginResponseDto
import com.ikemba.inventrar.user.data.dto.UserResponseDto

interface RemoteUserDataSource {
    suspend fun searchUser(
        query: String,
        resultLimit: Int? = null
    ): Result<UserResponseDto, DataError.Remote>

    suspend fun login(username: String, password: String): Result<UserLoginResponseDto, DataError.Remote>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<ResponseDto, DataError.Remote>
}