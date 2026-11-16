package com.sundroid.kobokobo.transactionHistory.domain

import com.sundroid.kobokobo.dashboard.utils.Util

data class TransactionHistory(
    val orders: List<OrderModel>? = emptyList(),
    val totalOrders: Int = 1,
    val totalPages: Int = 1,
    val limit: Int = Util.DEFAULT_LIMIT,
    val page:  Int = 1
)
