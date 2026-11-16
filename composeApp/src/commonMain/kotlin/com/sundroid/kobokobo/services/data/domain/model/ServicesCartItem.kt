package com.sundroid.kobokobo.services.data.domain.model

import kotlinx.serialization.Serializable


@Serializable
data class ServicesCartItem(
    val materialId: String = "",
    val materialName: String = "",
    var quantity: Int = 0,
    val unitPrice: Double = 0.0
) {
    fun getSubTotal(): Double = quantity * unitPrice
}
@Serializable
data class ServicesTransaction(
    val id: String = java.util.UUID.randomUUID().toString(),
    val reference: String = "",
    val businessId: String? = null,
    val staffId: String = "",
    val staffName: String = "",
    val items: List<ServicesCartItem> = emptyList(),
    val totalAmount: Double = 0.0,
    val paymentMethod: String = "",
    // Transactions created before payment tracking are treated as paid for compatibility.
    val isPaid: Boolean = true,
    val paymentProcessedBy: String? = null,
    val timestamp: Long = System.currentTimeMillis()
) {
    // Kotlin automatically generates a hidden secondary zero-argument constructor
    // because every property above has a default value.
}
