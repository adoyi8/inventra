package com.ikemba.inventrar.inventory.data.domain

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result

// In your domain/repository package (e.g., com.sundroid.domain.inventory.repository)


interface InventoryRepository {
    suspend fun createInventory(
        sku: String,
        itemName: String,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?, // This will be the URL after upload
        reorderLevel: Double?,
        organizationId: String,
        createdBy: String
    ): Result<ResponseDto, DataError.Remote>


    // --- Search ---
    suspend fun searchInventory(
        itemId: String? = null,
        sku: String? = null,
        itemName: String? = null,
        category: String? = null,
        organizationId: String,
        voided: Boolean? = null,
        status: String? = null
    ): Result<List<InventoryItem>, DataError.Remote>

    // --- Update ---
    suspend fun updateInventory(
        itemId: String,
        sku: String?,
        itemName: String?,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?, // New image URL or null if removed/unchanged
        reorderLevel: Double?,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote>

    // --- Update Quantity ---
    suspend fun updateInventoryQuantity(
        itemId: String,
        newQuantity: Double,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote>

    // --- Delete (Void) ---
    suspend fun deleteInventory(
        itemId: String,
        voidedBy: String
    ): Result<ResponseDto, DataError.Remote> // Unit indicates success without returning data

    // --- Activate ---
    suspend fun activateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote>

    // --- Deactivate ---
    suspend fun deactivateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote>

}