package com.ikemba.inventrar.dashboard.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {

    @Upsert
    suspend fun saveCategory(category: CategoryEntity)

    @Query("SELECT * FROM CategoryEntity")
    fun getAllCategories(): Flow<List<CategoryEntity>>

    @Upsert
    suspend fun saveItem(item: ItemEntity)

    @Query("SELECT * FROM ItemEntity")
    fun getAllAllItems(): Flow<List<ItemEntity>>

    @Query("DELETE FROM CategoryEntity")
    suspend fun deleteAllCategories()

    @Query("DELETE FROM ItemEntity")
    suspend fun deleteAllProducts()
}