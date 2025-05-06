package com.ikemba.inventrar.dashboard.data.database

import androidx.room.RoomDatabaseConstructor

@Suppress("NO_ACTUAL_FOR_EXPECT")
expect object ProductDatabaseConstructor: RoomDatabaseConstructor<ProductDatabase> {
    override fun initialize(): ProductDatabase
}