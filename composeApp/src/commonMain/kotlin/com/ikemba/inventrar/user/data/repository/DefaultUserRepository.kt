package com.ikemba.inventrar.user.data.repository

import androidx.sqlite.SQLiteException
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.core.domain.map
import com.ikemba.inventrar.user.data.database.UserDao
import com.ikemba.inventrar.user.data.dto.UserLoginResponseDto
import com.ikemba.inventrar.user.data.mappers.toUser
import com.ikemba.inventrar.user.data.mappers.toUserEntity
import com.ikemba.inventrar.user.data.network.RemoteUserDataSource
import com.ikemba.inventrar.user.domain.User
import com.ikemba.inventrar.user.domain.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class DefaultUserRepository(
    private val remoteUserDataSource: RemoteUserDataSource,
    private val userDao: UserDao
): UserRepository {

    override suspend fun login(username: String, password: String): Result<UserLoginResponseDto, DataError> {
       // val localResult = favoriteBookDao.getFavoriteBook(bookId)

        //return if(localResult == null) {
         val response =   remoteUserDataSource
                .login(username, password)
                .map {
                    it
                }

        return response
        //} else {
        //  Result.Success(localResult.description)
       // }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String,
        confirmPassword: String
    ): Result<ResponseDto?, DataError> {
        val response =   remoteUserDataSource
            .changePassword(oldPassword, newPassword, confirmPassword)
            .map {
                it
            }

        return response
    }

     override fun getAllUsers(): Flow<List<User>> {
        return userDao
            .getAllUsers().map {
                usersEntities ->
                usersEntities.map { it.toUser() }
            } }

    override suspend fun saveUserInLocalDb(user: User): EmptyResult<DataError.Local> {
        return try {
            userDao.upsert(user.toUserEntity())
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
    override suspend fun logout() {

    }

    override suspend fun deleteAllUsers(){
        userDao.deleteAllUsers()
    }

}