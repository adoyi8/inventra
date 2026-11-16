package com.sundroid.kobokobo.dashboard.data.database

import androidx.room.ConstructedBy
import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.sundroid.kobokobo.cart.data.database.CartDao
import com.sundroid.kobokobo.cart.data.database.CartEntity
import com.sundroid.kobokobo.cart.data.database.HeldCartDao
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestDao
import com.sundroid.kobokobo.cart.data.database.PostSalesRequestEntity


@Database(
    entities = [CategoryEntity::class, ItemEntity::class, CartEntity::class, PostSalesRequestEntity::class, HeldCartEntity::class],
    version = 19
)




@TypeConverters(StringListTypeConverter::class,CartItemTypeConverter::class)
@ConstructedBy(ProductDatabaseConstructor::class)
abstract class ProductDatabase: RoomDatabase() {
    abstract val productDao: ProductDao
    abstract val cartDao: CartDao
    abstract val postSalesRequestDao: PostSalesRequestDao
    abstract val heldCartDao: HeldCartDao
    companion object {
        const val DB_NAME = "product3.db"
    }
}
