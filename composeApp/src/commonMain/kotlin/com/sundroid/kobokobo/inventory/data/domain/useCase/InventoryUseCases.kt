package com.sundroid.kobokobo.inventory.data.domain.useCase

import com.sundroid.kobokobo.inventory.data.domain.model.*
import com.sundroid.kobokobo.inventory.data.domain.repository.InventoryRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

// Product use cases
class CreateProductUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(product: Product) = repo.createProduct(product)
}

class UpdateProductUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(product: Product) = repo.updateProduct(product)
}

class DeleteProductUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(productId: String, businessId: String) = repo.deleteProduct(productId, businessId)
}

class GetProductByIdUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(productId: String, businessId: String) = repo.getProductById(productId, businessId)
}

class GetProductsUseCase(private val repo: InventoryRepository) {
    operator fun invoke(businessId: String): Flow<List<Product>> = repo.getProducts(businessId)
}

class GetProductsByCategoryUseCase(private val repo: InventoryRepository) {
    operator fun invoke(categoryId: String, businessId: String): Flow<List<Product>> = repo.getProductsByCategory(categoryId, businessId)
}

// Category use cases
class CreateCategoryUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(category: Category) = repo.createCategory(category)
}

class UpdateCategoryUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(category: Category) = repo.updateCategory(category)
}

class DeleteCategoryUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(categoryId: String, businessId: String) = repo.deleteCategory(categoryId, businessId)
}

class GetCategoryByIdUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(categoryId: String, businessId: String) = repo.getCategoryById(categoryId, businessId)
}

class GetCategoriesUseCase(private val repo: InventoryRepository) {
    operator fun invoke(businessId: String): Flow<List<Category>> = repo.getCategories(businessId)
}

// Location use cases
class CreateLocationUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(location: Location) = repo.createLocation(location)
}

class UpdateLocationUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(location: Location) = repo.updateLocation(location)
}

class DeleteLocationUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(locationId: String, businessId: String) = repo.deleteLocation(locationId, businessId)
}

class GetLocationByIdUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(locationId: String, businessId: String) = repo.getLocationById(locationId, businessId)
}

class GetLocationsUseCase(private val repo: InventoryRepository) {
    operator fun invoke(businessId: String): Flow<List<Location>> = repo.getLocations(businessId)
}

// Stock use cases
class CreateStockMovementUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(movement: StockMovement) = repo.createStockMovement(movement)
}

class GetStockLevelUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(productId: String, locationId: String, businessId: String) = repo.getStockLevel(productId, locationId, businessId)
}

class UpdateStockLevelUseCase(private val repo: InventoryRepository) {
    suspend operator fun invoke(stockLevel: StockLevel) = repo.updateStockLevel(stockLevel)
}

class GetStockLevelsUseCase(private val repo: InventoryRepository) {
    operator fun invoke(businessId: String): Flow<List<StockLevel>> = repo.getStockLevels(businessId)
}

class GetStockMovementsUseCase(private val repo: InventoryRepository) {
    operator fun invoke(productId: String?, businessId: String): Flow<List<StockMovement>> = repo.getStockMovements(productId, businessId)
}









