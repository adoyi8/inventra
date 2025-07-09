package com.ikemba.inventrar.inventory.data.domain

// In your domain/usecase package (e.g., com.sundroid.domain.inventory.usecase)


import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Error
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import com.ikemba.inventrar.core.domain.Result

class CreateInventoryUseCase(
    private val inventoryRepository: InventoryRepository // Depends on the domain interface
) {
    // The 'invoke' operator function allows you to call the use case like a function: createInventoryUseCase(...)
    suspend operator fun invoke(
        sku: String,
        itemName: String,
        category: String?,
        unitOfMeasure: String?,
        quantity: Double?,
        purchasePricePerUnit: Double?,
        sellingPricePerUnit: Double?,
        imageUrl: String?, // This URL is passed from the ViewModel after image upload
        reorderLevel: Double?,
        organizationId: String,
        createdBy: String
    ): Result<ResponseDto, DataError.Remote> = withContext(Dispatchers.IO) { // Perform network/DB operations on IO dispatcher
        // Here you could add more business logic/validation if needed before calling the repository
        // For example, checking if itemName meets certain criteria, etc.
     

        inventoryRepository.createInventory(
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
}

// Example of a generic Result wrapper (you'd define this once in your domain layer)
