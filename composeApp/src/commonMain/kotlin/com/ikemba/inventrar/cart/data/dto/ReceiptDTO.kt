package com.ikemba.inventrar.cart.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable


@Serializable
data class ReceiptResponseDto(
@SerialName("response_code") val responseCode: Int,
    @SerialName("response_message") val responseMessage: String,
    @SerialName("data") val data: ReceiptDataDto?=null

)
@Serializable
data class ReceiptDataDto(
    @SerialName("items") val items: List<ReceiptItemDto>,
    @SerialName("summary") val summary: List<ReceiptSummaryDto>

)

@Serializable
data class ReceiptItemDto(
    @SerialName("product_id") val productId: String?="productId",
    @SerialName("product_name") val productName: String? ="productName",
    @SerialName("unit_price") val unitPrice: String,
    @SerialName("quantity") val quantity: String,
    @SerialName("unit_vat") val unitVat: String,
    @SerialName("unit_discount") val unitDiscount: String,
    @SerialName("sub_total") val subTotal: String
)

@Serializable
data class ReceiptSummaryDto(
    @SerialName("created") val created: String,
    @SerialName("order_no") val orderNo: String,
    @SerialName("action_time") val actionTime: String,
    @SerialName("taxTotal") val taxTotal: String,
    @SerialName("discountTotal") val discountTotal: String,
    @SerialName("subTotal") val subTotal: String,
    @SerialName("grandTotal") val grandTotal: String,
    @SerialName("sales_person") val salesPerson: String,
    @SerialName("business_name") val businessName: String,
    @SerialName("logo") val logo: String,
    @SerialName("business_address") val businessAddress: String,
    @SerialName("vat_rate") val vatRate: String,
    @SerialName("currency_code") val currencyCode: String,
    @SerialName("currency_entity") val currencyEntity: String

)
