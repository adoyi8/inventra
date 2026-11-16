package com.sundroid.kobokobo.inventory.data.domain.model

import java.util.UUID

data class StockMovement(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val fromLocationId: String? = null, // null for new stock
    val toLocationId: String,
    val quantity: Double,
    val movementType: MovementType,
    val businessId: String,
    val staffId: String,
    val reason: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null
)

enum class MovementType {
    IN, // New stock added
    OUT, // Stock removed
    TRANSFER, // Stock moved between locations
    ADJUSTMENT // Stock count adjustment
}









