package com.ikemba.inventrar.cart.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Update
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface PostSalesRequestDao {

    @Upsert
    suspend fun saveSales(sales: PostSalesRequestEntity)

    @Query("SELECT * FROM PostSalesRequestEntity")
    fun getAllSales(): Flow<List<PostSalesRequestEntity>>


    @Query("DELETE FROM PostSalesRequestEntity")
    suspend fun deleteAllSales()


    @Query("DELETE FROM PostSalesRequestEntity WHERE reference = :reference")
    suspend fun deleteSales(reference: String)

    @Update
    suspend fun updateSales(sales: PostSalesRequestEntity)
}