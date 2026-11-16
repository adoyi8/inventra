package com.sundroid.kobokobo.user.domain

import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.user.data.dto.UserLoginResponseDto
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun login(userName: String, password: String): Result<UserLoginResponseDto?, DataError>
    suspend fun changePassword(oldPassword: String, newPassword: String, confirmPassword: String): Result<ResponseDto?, DataError>
    suspend fun logout()
    fun getAllUsers(): Flow<List<User>>
    suspend fun saveUserInLocalDb(user: User): EmptyResult<DataError.Local>
    suspend fun deleteAllUsers()
    suspend fun checkAndCreateUserInFirestore(firebaseUser: dev.gitlive.firebase.auth.FirebaseUser?): kotlin.Result<Unit>
    fun getMyStaffEntryFlow(userId: String, businessId: String): Flow<Staff?>

}