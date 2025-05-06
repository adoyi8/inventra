package com.ikemba.inventrar.heldOrder.data.mappers

import com.ikemba.inventrar.heldOrder.data.dto.HeldOrderDto
import com.ikemba.inventrar.transactionHistory.domain.OrderModel
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistory


fun HeldOrderDto.toTransactionHistory(): TransactionHistory {
     return TransactionHistory(orders = this.data?.history?.map { OrderModel(orderId = it.orderId, created = it.created, paymentMethod = it.paymentMethod, totalAmount = it.totalAmount, )}, page = this.data!!.page, limit = this.data!!.limit, totalPages = this.data!!.totalPages, totalOrders = this.data!!.totalOrders);

}