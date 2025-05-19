package com.ikemba.inventrar.dashboard.domain

data class Item(
    val id: String,
    val name: String,
    val unitPrice: Double,
    val image: String?,
    val categoryId: String,
    val discount: Double?,
    val vat: Double = 0.0,
    val sku: String
)