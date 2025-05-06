package com.ikemba.inventrar.transactionHistory.domain

data class TransactionHistory(
    val orders: List<OrderModel>? = emptyList(),
    val totalOrders: Int = 1,
    val totalPages: Int = 1,
    val limit: Int = 10,
    val page:  Int = 1
)
