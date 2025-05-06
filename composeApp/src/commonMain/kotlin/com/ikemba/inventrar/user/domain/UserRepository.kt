package com.ikemba.inventrar.user.domain

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.user.data.dto.UserLoginResponseDto
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(userName: String, password: String): Result<UserLoginResponseDto?, DataError>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<ResponseDto?, DataError>
    suspend fun logout()
    fun getAllUsers(): Flow<List<User>>
    suspend fun saveUserInLocalDb(user: User): EmptyResult<DataError.Local>
    suspend fun deleteAllUsers()

}