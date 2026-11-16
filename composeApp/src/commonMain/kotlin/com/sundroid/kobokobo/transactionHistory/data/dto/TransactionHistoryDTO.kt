package com.sundroid.kobokobo.transactionHistory.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TransactionHistoryDto(
    @SerialName("response_code") val responseCode: Int,
    @SerialName("response_message") val responseMessage: String,
    @SerialName("data") val data: TransactionData? = null
)

@Serializable
data class TransactionData(
    @SerialName("history") val history: List<Transaction>,
    @SerialName("page") val page: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total_orders") val totalOrders: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class Transaction(
    @SerialName("order_id") val orderId: String,
    @SerialName("created") val created: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("total_amount") val totalAmount: String,
    @SerialName("createdBy") val createdBy: String,
    @SerialName("payment_method_text") val paymentMethodText: String? = null,
    @SerialName("is_paid") val isPaid: Boolean = true,
    @SerialName("payment_processed_by") val paymentProcessedBy: String? = null
)
