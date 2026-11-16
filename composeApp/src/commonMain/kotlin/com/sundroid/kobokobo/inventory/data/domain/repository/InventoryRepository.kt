package com.sundroid.kobokobo.inventory.data.domain.repository

import com.sundroid.kobokobo.inventory.data.domain.model.*
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface InventoryRepository {
    // Product operations
    suspend fun createProduct(product: Product): Result<Product, DataError>
    suspend fun updateProduct(product: Product): Result<Product, DataError>
    suspend fun deleteProduct(productId: String, businessId: String): EmptyResult<DataError>
    suspend fun getProductById(productId: String, businessId: String): Result<Product?, DataError>
    fun getProducts(businessId: String): Flow<List<Product>>
    fun getProductsByCategory(categoryId: String, businessId: String): Flow<List<Product>>
    
    // Category operations
    suspend fun createCategory(category: Category): Result<Category, DataError>
    suspend fun updateCategory(category: Category): Result<Category, DataError>
    suspend fun deleteCategory(categoryId: String, businessId: String): EmptyResult<DataError>
    suspend fun getCategoryById(categoryId: String, businessId: String): Result<Category?, DataError>
    fun getCategories(businessId: String): Flow<List<Category>>
    
    // Location operations
    suspend fun createLocation(location: Location): Result<Location, DataError>
    suspend fun updateLocation(location: Location): Result<Location, DataError>
    suspend fun deleteLocation(locationId: String, businessId: String): EmptyResult<DataError>
    suspend fun getLocationById(locationId: String, businessId: String): Result<Location?, DataError>
    fun getLocations(businessId: String): Flow<List<Location>>
    
    // Stock operations
    suspend fun createStockMovement(movement: StockMovement): Result<StockMovement, DataError>
    suspend fun getStockLevel(productId: String, locationId: String, businessId: String): Result<StockLevel?, DataError>
    suspend fun updateStockLevel(stockLevel: StockLevel): Result<StockLevel, DataError>
    fun getStockLevels(businessId: String): Flow<List<StockLevel>>
    fun getStockMovements(productId: String?, businessId: String): Flow<List<StockMovement>>
}









