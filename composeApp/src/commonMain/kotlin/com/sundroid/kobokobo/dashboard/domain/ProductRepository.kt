package com.sundroid.kobokobo.dashboard.domain

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.dashboard.data.database.CategoryEntity
import com.sundroid.kobokobo.dashboard.data.database.ItemEntity
import com.sundroid.kobokobo.dashboard.data.dto.ProductResponseDto
import kotlinx.coroutines.flow.Flow
import com.sundroid.kobokobo.core.domain.Result

interface ProductRepository {

    suspend fun getProducts(businessId: String): Result<ProductResponseDto,DataError.Remote>
    suspend fun saveCategory(category: CategoryEntity): EmptyResult<DataError.Local>
    suspend fun saveItem(item: ItemEntity): EmptyResult<DataError.Local>
    suspend fun clear()
    fun getAllCategory(): Flow<List<CategoryEntity>>
    fun getAllItems(): Flow<List<ItemEntity>>
}