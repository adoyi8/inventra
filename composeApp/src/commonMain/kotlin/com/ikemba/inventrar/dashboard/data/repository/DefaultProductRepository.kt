package com.ikemba.inventrar.dashboard.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.data.database.CategoryEntity
import com.ikemba.inventrar.dashboard.data.database.ItemEntity
import com.ikemba.inventrar.dashboard.data.database.ProductDao
import com.ikemba.inventrar.dashboard.data.dto.CategoryDto
import com.ikemba.inventrar.dashboard.data.dto.ItemDto
import com.ikemba.inventrar.dashboard.data.dto.ProductDto
import com.ikemba.inventrar.dashboard.data.dto.ProductResponseDto
import com.ikemba.inventrar.dashboard.data.mappers.toCategory
import com.ikemba.inventrar.dashboard.data.mappers.toCategoryEntity
import com.ikemba.inventrar.dashboard.data.mappers.toItem
import com.ikemba.inventrar.dashboard.data.mappers.toItemEntity
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class DefaultProductRepository(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getProducts(accessToken: String): Result<ProductResponseDto, DataError.Remote> {
        return try {
            // Fetch categories and items from Firestore
            val categoriesSnapshot = firestore.collection("categories").get().await()
            val itemsSnapshot = firestore.collection("products").get().await()

            val categories = categoriesSnapshot.documents.mapNotNull { doc ->
                try {
                    CategoryDto(
                        categoryId = doc.getString("sub_id") ?: doc.id,
                        categoryName = doc.getString("sub_name") ?: (doc.getString("name") ?: ""),
                        categoryCode = doc.getString("sub_code") ?: (doc.getString("code") ?: doc.id)
                    )
                } catch (e: Exception) { null }
            }

            val items = itemsSnapshot.documents.mapNotNull { doc ->
                try {
                    ItemDto(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        unitPrice = (doc.get("unit_price") ?: doc.get("unitPrice") ?: 0.0).toString(),
                        image = null,
                        categoryId = doc.getString("category_id") ?: doc.getString("categoryId") ?: "",
                        discount = (doc.get("discount") ?: 0.0).toString(),
                        vat = (doc.get("vat") ?: 0.0).toString(),
                        sku = doc.getString("sku") ?: ""
                    )
                } catch (e: Exception) { null }
            }

            Result.Success(
                ProductResponseDto(
                    responseCode = 0,
                    responseMessage = "OK",
                    product = ProductDto(categoryDto = categories, itemDto = items)
                )
            )
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun saveCategory(category: CategoryEntity): EmptyResult<DataError.Local> {
        return try {
            productDao.saveCategory(category)
            Result.Success(Unit)
        } catch(e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override suspend fun saveItem(item: ItemEntity): EmptyResult<DataError.Local> {
        return try {
            productDao.saveItem(item)
            Result.Success(Unit)
        } catch(e: Exception) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }

    override fun getAllCategory(): Flow<List<CategoryEntity>> {
        return productDao.getAllCategories()
    }

    override fun getAllItems(): Flow<List<ItemEntity>> {
        return productDao.getAllAllItems()
    }
    override suspend fun clear(){
        productDao.deleteAllCategories()
        productDao.deleteAllProducts()

    }

}