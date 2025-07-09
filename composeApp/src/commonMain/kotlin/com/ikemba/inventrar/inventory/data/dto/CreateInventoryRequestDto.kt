package com.ikemba.inventrar.inventory.data.dto

// In your data/dto package (e.g., com.sundroid.data.inventory.dto)



import kotlinx.serialization.Serializable

@Serializable
data class CreateInventoryRequestDto(
    val sku: String,
    val itemName: String,
    val category: String? = null,
    val unitOfMeasure: String? = null,
    val quantity: Double? = null,
    val purchasePricePerUnit: Double? = null,
    val sellingPricePerUnit: Double? = null,
    val imageUrl: String? = null,
    val reorderLevel: Double? = null,
    val organizationId: String,
    val createdBy: String
)