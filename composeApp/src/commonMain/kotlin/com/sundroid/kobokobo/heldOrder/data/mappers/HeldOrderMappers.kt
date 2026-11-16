package com.sundroid.kobokobo.heldOrder.data.mappers

import com.sundroid.kobokobo.heldOrder.data.dto.HeldOrderDto
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistory


fun HeldOrderDto.toTransactionHistory(): TransactionHistory {
     return TransactionHistory(orders = this.data?.history?.map { OrderModel(orderId = it.orderId, created = it.created, paymentMethod = it.paymentMethod, totalAmount = it.totalAmount, )}, page = this.data!!.page, limit = this.data!!.limit, totalPages = this.data!!.totalPages, totalOrders = this.data!!.totalOrders);

}