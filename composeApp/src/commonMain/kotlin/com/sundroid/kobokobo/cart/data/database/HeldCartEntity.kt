package com.sundroid.kobokobo.cart.data.database

// Replace with your actual package
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sundroid.kobokobo.cart.domain.CartItem

@Entity(tableName = "held_carts")
data class HeldCartEntity(
    @PrimaryKey
    val reference: String, // Use the transaction reference as the ID
    val timestamp: Long = System.currentTimeMillis(),
    val items: List<CartItem>, // This will be converted using the TypeConverter
    val totalAmount: Double
)