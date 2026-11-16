package com.sundroid.kobokobo.dashboard.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.data.database.CategoryEntity
import com.sundroid.kobokobo.dashboard.data.database.ItemEntity
import com.sundroid.kobokobo.dashboard.data.database.ProductDao
import com.sundroid.kobokobo.dashboard.data.dto.CategoryDto
import com.sundroid.kobokobo.dashboard.data.dto.ItemDto
import com.sundroid.kobokobo.dashboard.data.dto.ProductDto
import com.sundroid.kobokobo.dashboard.data.dto.ProductResponseDto
import com.sundroid.kobokobo.dashboard.domain.ProductRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.tasks.await

class DefaultProductRepository(
    private val firestore: FirebaseFirestore,
    private val productDao: ProductDao
): ProductRepository
{
    override suspend fun getProducts(businessId: String): Result<ProductResponseDto, DataError.Remote> {
        return try {
            // Define the parent document for this business
            println("John Wick 1 ")
            val businessDocRef = firestore.collection("businesses").document(businessId)

            // Fetch categories and items from subcollections of the business document
            val categoriesSnapshot = businessDocRef.collection("categories").get().await()
            val itemsSnapshot = businessDocRef.collection("products").get().await()

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
                    val costCP = (doc.get("cost_price") ?: doc.get("costPrice") ?: 0.0).toString()
                    println("John Wick "+ costCP)
                    ItemDto(
                        id = doc.getString("id") ?: doc.id,
                        name = doc.getString("name") ?: "",
                        unitPrice = (doc.get("unit_price") ?: doc.get("unitPrice") ?: 0.0).toString(),
                        costPrice = (doc.get("cost_price") ?: doc.get("costPrice") ?: 0.0).toString(),
                        image = null,
                        categoryId = doc.getString("category_id") ?: doc.getString("categoryId") ?: "",
                        discount = (doc.get("discount") ?: 0.0).toString(),
                        vat = (doc.get("vat") ?: 0.0).toString(),
                        sku = doc.getString("sku") ?: "",
                        stock = (doc.get("stock") ?: 0.0).toString(),
                        sortOrder = (doc.get("sort_order") ?: doc.get("sortOrder") ?: 0).toString()
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