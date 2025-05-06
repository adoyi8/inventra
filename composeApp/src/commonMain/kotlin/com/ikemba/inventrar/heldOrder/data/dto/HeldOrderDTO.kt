package com.ikemba.inventrar.heldOrder.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class HeldOrderDto(
    @SerialName("response_code") val responseCode: Int,
    @SerialName("response_message") val responseMessage: String,
    @SerialName("data") val data: HeldOrderData? = null
)

@Serializable
data class HeldOrderData(
    @SerialName("history") val history: List<HeldOrder>,
    @SerialName("page") val page: Int,
    @SerialName("limit") val limit: Int,
    @SerialName("total_orders") val totalOrders: Int,
    @SerialName("total_pages") val totalPages: Int
)

@Serializable
data class HeldOrder(
    @SerialName("order_id") val orderId: String,
    @SerialName("created") val created: String,
    @SerialName("payment_method") val paymentMethod: String,
    @SerialName("total_amount") val totalAmount: String
)
