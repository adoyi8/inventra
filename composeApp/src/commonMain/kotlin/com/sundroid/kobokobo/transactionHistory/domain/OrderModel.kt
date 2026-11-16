package com.sundroid.kobokobo.transactionHistory.domain



data class OrderModel(
    val orderId: String,
    val created: String,
    val paymentMethod: String,
    val totalAmount: String,
    val paymentMethodText: String = "",
    val isPaid: Boolean = true,
    val paymentProcessedBy: String? = null,
    val items: List<com.sundroid.kobokobo.cart.domain.CartItem> = emptyList()
)
