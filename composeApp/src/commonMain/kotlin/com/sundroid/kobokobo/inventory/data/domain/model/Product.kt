package com.sundroid.kobokobo.inventory.data.domain.model

import java.util.UUID

data class Product(
    val id: String = UUID.randomUUID().toString(),
    val name: String="",
    val unitPrice: Double=0.0,
    val costPrice: Double = 0.0,
    val categoryId: String="",
    val discount: Double? = 0.0,
    val vat: Double = 0.0,
    val sku: String=UUID.randomUUID().toString(),
    val businessId: String="",
    val description: String = "",
    val isActive: Boolean = true,
    val stock: Double? = 0.0,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = UUID.randomUUID().toString(),
    val updatedBy: String? = UUID.randomUUID().toString(),
    val sortOrder: Int = 0
)









