package com.ikemba.inventrar.user.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {

    @Upsert
    suspend fun upsert(user: UserEntity)

    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): Flow<List<UserEntity>>

    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUser(id: String): UserEntity?

    @Query("DELETE FROM UserEntity WHERE id = :id")
    suspend fun deleteUser(id: String)

    @Query("DELETE FROM UserEntity")
    suspend fun deleteAllUsers()
}