package com.ikemba.inventrar.user.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ikemba.inventrar.business.data.database.BusinessDao
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.dashboard.data.database.StringListTypeConverter

@Database(
    entities = [UserEntity::class, Business::class],
    version = 4
)
@TypeConverters(
    StringListTypeConverter::class
)
@ConstructedBy(UserDatabaseConstructor::class)
abstract class UserDatabase: RoomDatabase() {
    abstract val userDao: UserDao
    abstract val businessDao: BusinessDao


    companion object {
        const val DB_NAME = "user.db"
    }
}