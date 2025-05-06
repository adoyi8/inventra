package com.ikemba.inventrar.cart.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {

    @Upsert
    suspend fun saveCart(cart: CartEntity)

    @Query("SELECT * FROM CartEntity")
    fun getAllCartItems(): Flow<List<CartEntity>>


    @Query("DELETE FROM CartEntity")
    suspend fun deleteAllCartItems()


    @Query("DELETE FROM CartEntity WHERE cartId = :cartId")
    suspend fun deleteCartItem(cartId: String)

    @Update
    suspend fun updateCartItem(cart: CartEntity)


}