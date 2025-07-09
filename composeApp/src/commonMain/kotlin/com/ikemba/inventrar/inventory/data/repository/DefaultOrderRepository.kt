package com.ikemba.inventrar.inventory.data.repository


import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result

import com.ikemba.inventrar.inventory.data.domain.InventoryItem
import com.ikemba.inventrar.inventory.data.domain.InventoryRepository
import com.ikemba.inventrar.inventory.network.KtorRemoteInventoryDataSource

class DefaultInventoryRepository(
    private val remoteInventoryDataSource: KtorRemoteInventoryDataSource,
): InventoryRepository {
    override suspend fun createInventory(
        sku: String,
        itemName: String,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?,
        reorderLevel: Double?,
        organizationId: String,
        createdBy: String
    ): Result<ResponseDto, DataError.Remote> {
        return remoteInventoryDataSource.createInventory(
            sku,
            itemName,
            category,
            unitOfMeasure,
            quantity,
            purchasePricePerUnit,
            sellingPricePerUnit,
            imageUrl,
            reorderLevel,
            organizationId,
            createdBy
        )
    }

    override suspend fun searchInventory(
        itemId: String?,
        sku: String?,
        itemName: String?,
        category: String?,
        organizationId: String,
        voided: Boolean?,
        status: String?
    ): Result<List<InventoryItem>, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun updateInventory(
        itemId: String,
        sku: String?,
        itemName: String?,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?,
        reorderLevel: Double?,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun updateInventoryQuantity(
        itemId: String,
        newQuantity: Double,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun deleteInventory(
        itemId: String,
        voidedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun activateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun deactivateInventory(
        itemId: String,
        updatedBy: String
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


}

