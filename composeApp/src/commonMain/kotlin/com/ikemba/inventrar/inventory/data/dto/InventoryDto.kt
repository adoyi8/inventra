package com.ikemba.inventrar.inventory.data.dto

// In your data/dto package (e.g., com.sundroid.data.inventory.dto)



import com.ikemba.inventrar.inventory.data.domain.InventoryItem
import kotlinx.serialization.Serializable
import java.time.LocalDateTime

// Use kotlinx.datetime for multiplatform dates

@Serializable
data class InventoryDto(
    val itemId: String,
    val sku: String,
    val itemName: String,
    val itemDisplayId: String,
    val category: String? = null,
    val unitOfMeasure: String? = null,
    val quantity: Double? = null,
    val purchasePricePerUnit: Double? = null,
    val sellingPricePerUnit: Double? = null,
    val imageUrl: String? = null,
    val reorderLevel: Double? = null,
    val organizationId: String,
    val organizationName: String,
    val status: String,
    val createdBy: String,
    val updatedBy: String? = null,
    val voidedBy: String? = null,
    val voided: Boolean
)

// Extension function to map DTO to Domain Entity
fun InventoryDto.toDomain(): InventoryItem {
    return InventoryItem(
        id = this.itemId,
        sku = this.sku,
        name = this.itemName,
        displayId = this.itemDisplayId,
        category = this.category,
        unitOfMeasure = this.unitOfMeasure,
        quantity = this.quantity ?: 0.0,
        purchasePricePerUnit = this.purchasePricePerUnit,
        sellingPricePerUnit = this.sellingPricePerUnit,
        imageUrl = this.imageUrl,
        reorderLevel = this.reorderLevel,
        organizationId = this.organizationId,
        organizationName = this.organizationName,
        status = this.status,
        createdBy = this.createdBy,
        updatedBy = this.updatedBy,
        voidedBy = this.voidedBy,
        isVoided = this.voided
    )
}