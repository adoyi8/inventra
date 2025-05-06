package com.ikemba.inventrar.user.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


actual class UserDatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<UserDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(UserDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}