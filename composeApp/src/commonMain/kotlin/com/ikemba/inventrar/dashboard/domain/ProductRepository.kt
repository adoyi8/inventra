package com.ikemba.inventrar.dashboard.domain

import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.dashboard.data.database.CategoryEntity
import com.ikemba.inventrar.dashboard.data.database.ItemEntity
import com.ikemba.inventrar.dashboard.data.dto.ProductResponseDto
import kotlinx.coroutines.flow.Flow
import com.ikemba.inventrar.core.domain.Result

interface ProductRepository {

    suspend fun getProducts(accessToken: String): Result<ProductResponseDto,DataError.Remote>
    suspend fun saveCategory(category: CategoryEntity): EmptyResult<DataError.Local>
    suspend fun saveItem(item: ItemEntity): EmptyResult<DataError.Local>
    suspend fun clear()
    fun getAllCategory(): Flow<List<CategoryEntity>>
    fun getAllItems(): Flow<List<ItemEntity>>
}