package com.sundroid.kobokobo.dashboard.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class ItemEntity(
    @PrimaryKey(autoGenerate = false)
    val id: String,
    val name: String,
    val unitPrice: Double,
    val image: String?,
    val categoryId: String,
    val discount: Double?,
    val vat: Double = 0.0,
    val sku: String,
    val stock: Double = 0.0,
    val costPrice: Double = 0.0,
    val sortOrder: Int = 0
)