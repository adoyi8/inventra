package com.ikemba.inventrar.business.data.database

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import kotlinx.coroutines.flow.Flow

@Dao
interface BusinessDao {

    @Upsert
    suspend fun upsert(business: Business)

    @Query("SELECT * FROM Business")
    fun getAllBusiness(): Flow<List<Business>>

    @Query("SELECT * FROM Business WHERE id = :id")
    suspend fun getBusiness(id: String): Business?

    @Query("DELETE FROM Business WHERE id = :id")
    suspend fun deleteBusiness(id: String)

    @Query("DELETE FROM Business")
    suspend fun deleteAllBusiness()
}