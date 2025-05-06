package com.ikemba.inventrar.user.data.database

import androidx.room.RoomDatabase

expect class UserDatabaseFactory {
    fun create(): RoomDatabase.Builder<UserDatabase>
}