package com.ikemba.inventrar.inventory.data.domain

import java.time.LocalDateTime

data class InventoryItem(
    val id: String,
    val sku: String,
    val name: String,
    val displayId: String,
    val category: String?,
    val unitOfMeasure: String?,
    val quantity: Double,
    val purchasePricePerUnit: Double?,
    val sellingPricePerUnit: Double?,
    val imageUrl: String?,
    val reorderLevel: Double?,
    val organizationId: String,
    val organizationName: String,
    val status: String,
    val createdBy: String,

    val updatedBy: String?,

    val voidedBy: String?,
    val isVoided: Boolean
)