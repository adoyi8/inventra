package com.sundroid.kobokobo.inventory.data.domain.model

import java.util.UUID

data class StockLevel(
    val id: String = UUID.randomUUID().toString(),
    val productId: String,
    val locationId: String,
    val quantity: Double,
    val businessId: String,
    val lastUpdated: Long = System.currentTimeMillis(),
    val updatedBy: String? = null
)









