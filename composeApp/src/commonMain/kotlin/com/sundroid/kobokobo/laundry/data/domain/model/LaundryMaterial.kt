package com.sundroid.kobokobo.laundry.data.domain.model

import java.util.UUID

data class LaundryMaterial(
    val id: String = UUID.randomUUID().toString(),
    val businessId: String? = null,
    val name: String = "",
    val washingPrice: Double = 0.0,
    val ironingPrice: Double = 0.0,
    val washAndIronPrice: Double = 0.0,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = UUID.randomUUID().toString(),
    val updatedBy: String? = UUID.randomUUID().toString()
)
