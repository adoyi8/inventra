package com.ikemba.inventrar.admin.presentation

data class AddInventoryItemFormState(
    val sku: String = "",
    val itemName: String= "",
    val category: String= "",
    val unitOfMeasure: String= "",
    val quantity: String= "",
    val purchasePrice: String= "",
    val sellingPrice: String= "",
    var imageUrl: String?= "", // This would be the URL after upload
    val reorderLevel: String= "",
    val organizationId: String= "",
    val createdBy: String= ""
)