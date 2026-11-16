package com.sundroid.kobokobo.transactionHistory.data.mappers

import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionHistoryDto
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistory


fun TransactionHistoryDto.toTransactionHistory(): TransactionHistory {
     return TransactionHistory(orders = this.data?.history?.map { OrderModel(orderId = it.orderId, created = it.created, paymentMethod = it.paymentMethod, totalAmount = it.totalAmount, paymentMethodText = it.paymentMethodText ?: "", isPaid = it.isPaid, paymentProcessedBy = it.paymentProcessedBy)}, page = this.data!!.page, limit = this.data!!.limit, totalPages = this.data!!.totalPages, totalOrders = this.data!!.totalOrders);

}
