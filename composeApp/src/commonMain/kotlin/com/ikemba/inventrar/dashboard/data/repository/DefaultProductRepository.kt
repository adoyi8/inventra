package com.ikemba.inventrar.dashboard.data.repository

import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.core.domain.map
import com.ikemba.inventrar.dashboard.data.database.CategoryEntity
import com.ikemba.inventrar.dashboard.data.database.ItemEntity
import com.ikemba.inventrar.dashboard.data.database.ProductDao
import com.ikemba.inventrar.dashboard.data.dto.ProductResponseDto
import com.ikemba.inventrar.dashboard.data.mappers.toCategory
import com.ikemba.inventrar.dashboard.data.mappers.toCategoryEntity
import com.ikemba.inventrar.dashboard.data.mappers.toItem
import com.ikemba.inventrar.dashboard.data.mappers.toItemEntity
import com.ikemba.inventrar.dashboard.data.network.RemoteProductDataSource
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import kotlinx.coroutines.flow.Flow

class DefaultProductRepository(
    private val remoteProductDataSource: RemoteProductDataSource,
    private val productDao: ProductDao
): ProductRepository {
    override suspend fun getProducts(accessToken: String): Result<ProductResponseDto, DataError.Remote> {
        return remoteProductDataSource
            .getProducts(accessToken)

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

}