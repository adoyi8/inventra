package com.ikemba.inventrar.dashboard.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.ikemba.inventrar.cart.data.database.CartDao
import com.ikemba.inventrar.cart.data.database.CartEntity
import com.ikemba.inventrar.cart.data.database.PostSalesRequestDao
import com.ikemba.inventrar.cart.data.database.PostSalesRequestEntity


@Database(
    entities = [CategoryEntity::class, ItemEntity::class, CartEntity::class, PostSalesRequestEntity::class],
    version = 7
)




@TypeConverters(StringListTypeConverter::class)
@ConstructedBy(ProductDatabaseConstructor::class)
abstract class ProductDatabase: RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val cartDao: CartDao
    abstract val postSalesRequestDao: PostSalesRequestDao
    companion object {
        const val DB_NAME = "product2.db"
    }
}