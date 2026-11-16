package com.sundroid.kobokobo.user.data.database

import androidx.room.RoomDatabase

expect class UserDatabaseFactory {
    fun create(): RoomDatabase.Builder<UserDatabase>
}