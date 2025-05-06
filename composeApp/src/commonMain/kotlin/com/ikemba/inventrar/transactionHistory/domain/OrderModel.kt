package com.ikemba.inventrar.transactionHistory.domain



data class OrderModel(
    val orderId: String,
    val created: String,
    val paymentMethod: String,
    val totalAmount: String,
)