package com.sundroid.kobokobo.cart.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface HeldCartDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHeldCart(heldCart: HeldCartEntity)

    @Delete
    suspend fun deleteHeldCart(heldCart: HeldCartEntity)

    @Query("SELECT * FROM held_carts ORDER BY timestamp DESC")
    fun getAllHeldCarts(): Flow<List<HeldCartEntity>>
}