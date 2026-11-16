package com.sundroid.kobokobo.user.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sundroid.kobokobo.business.data.database.Business
import com.sundroid.kobokobo.business.data.database.BusinessDao
import com.sundroid.kobokobo.dashboard.data.database.StringListTypeConverter

@Database(
    entities = [UserEntity::class, Business::class],
    version = 10
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