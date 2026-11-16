package com.sundroid.kobokobo.damages.data.domain

import java.util.UUID

data class Damage(
    val id: String = UUID.randomUUID().toString(),
    val businessId: String,
    val productId: String,
    val productName: String,
    val quantity: Int,
    val costPrice: Double, // Cost price at the time of damage
    val staffId: String,
    val staffName: String,
    val reason: String = "",
    val createdAt: Long = System.currentTimeMillis()
) {
    val totalCost: Double
        get() = quantity * costPrice
}