package com.ikemba.inventrar.transactionHistory.data.mappers

import com.ikemba.inventrar.transactionHistory.data.dto.TransactionHistoryDto
import com.ikemba.inventrar.transactionHistory.domain.OrderModel
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistory


fun TransactionHistoryDto.toTransactionHistory(): TransactionHistory {
     return TransactionHistory(orders = this.data?.history?.map { OrderModel(orderId = it.orderId, created = it.created, paymentMethod = it.paymentMethod, totalAmount = it.totalAmount, )}, page = this.data!!.page, limit = this.data!!.limit, totalPages = this.data!!.totalPages, totalOrders = this.data!!.totalOrders);

}