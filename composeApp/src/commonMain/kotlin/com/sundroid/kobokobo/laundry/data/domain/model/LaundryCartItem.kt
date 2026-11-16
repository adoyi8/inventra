package com.sundroid.kobokobo.laundry.data.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class LaundryCartItem(
    val materialId: String = "",
    val materialName: String = "",
    val serviceType: String = "", // "Washing Only", "Ironing Only", "Wash & Iron"
    var quantity: Int = 0,
    val unitPrice: Double = 0.0
) {
    fun getSubTotal(): Double = quantity * unitPrice
}
@Serializable
data class LaundryTransaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val reference: String = "",
    val businessId: String? = null,
    val staffId: String = "",
    val staffName: String = "",
    val items: List<LaundryCartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    val timestamp: Long = System.currentTimeMillis()
) {
    // Kotlin automatically generates a hidden secondary zero-argument constructor
    // because every property above has a default value.
}
