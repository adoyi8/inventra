package com.ikemba.inventrar.heldOrder.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SingleHeldOrderDto(
    @SerialName("response_code") val responseCode: Int,
    @SerialName("response_message") val responseMessage: String,
    @SerialName("data") val data: List<SingleHeldOrder>
)


@Serializable
data class SingleHeldOrder(
    @SerialName("order_id") val orderId: String,
    @SerialName("product_id") val productId: String,
    @SerialName("product_name") val productName: String,
    @SerialName("subtotal") val subTotal: String,
    @SerialName("qty") val qty: String,
    @SerialName("vat_rate") val vatRate: String
)