package com.ikemba.inventrar.cart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CartEntity(
    @PrimaryKey(autoGenerate = false)
    val cartId: String,
   // val cartItems: List<CartItem>,
    val paymentMethod: String?,
    val reference: String?
)