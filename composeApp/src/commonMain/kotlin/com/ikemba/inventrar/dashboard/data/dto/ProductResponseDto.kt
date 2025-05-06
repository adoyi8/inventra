package com.ikemba.inventrar.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductResponseDto(
    @SerialName("response_code") val responseCode: Int,
    @SerialName("response_message") val responseMessage: String?,
    @SerialName("data") val product: ProductDto? = null,

)
