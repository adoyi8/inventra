package com.ikemba.inventrar.dashboard.data.database

import android.content.Context
import androidx.room.Room
import androidx.room.RoomDatabase


actual class ProductDatabaseFactory(
    private val context: Context
) {
    actual fun create(): RoomDatabase.Builder<ProductDatabase> {
        val appContext = context.applicationContext
        val dbFile = appContext.getDatabasePath(ProductDatabase.DB_NAME)

        return Room.databaseBuilder(
            context = appContext,
            name = dbFile.absolutePath
        )
    }
}