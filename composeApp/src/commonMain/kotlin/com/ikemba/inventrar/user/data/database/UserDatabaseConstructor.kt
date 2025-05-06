package com.ikemba.inventrar.user.data.database

import androidx.room.RoomDatabaseConstructor


@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object UserDatabaseConstructor: RoomDatabaseConstructor<UserDatabase> {
    override fun initialize(): UserDatabase
}