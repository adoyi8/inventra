package com.ikemba.inventrar.dashboard.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class CategoryEntity(
    @PrimaryKey(autoGenerate = false)
    val categoryId: String,
    val categoryName: String,
    val categoryCode: String,
)
