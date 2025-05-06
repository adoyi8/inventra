package com.ikemba.inventrar.dashboard.data.database

import androidx.room.RoomDatabase

expect class ProductDatabaseFactory {
    fun create(): RoomDatabase.Builder<ProductDatabase>
}