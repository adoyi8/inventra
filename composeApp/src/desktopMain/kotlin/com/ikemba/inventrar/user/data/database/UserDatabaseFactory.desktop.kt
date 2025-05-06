package com.ikemba.inventrar.user.data.database

import androidx.room.Room
import androidx.room.RoomDatabase


import java.io.File

actual class UserDatabaseFactory {
    actual fun create(): RoomDatabase.Builder<UserDatabase> {
        val os = System.getProperty("os.name").lowercase()
        val userHome = System.getProperty("user.home")
        val appDataDir = when {
            os.contains("win") -> File(System.getenv("APPDATA"), "Inventra")
            os.contains("mac") -> File(userHome, "Library/Application Support/Inventra")
            else -> File(userHome, ".local/share/Inventra")
        }

        if(!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, UserDatabase.DB_NAME)
        return Room.databaseBuilder(dbFile.absolutePath)
    }
}