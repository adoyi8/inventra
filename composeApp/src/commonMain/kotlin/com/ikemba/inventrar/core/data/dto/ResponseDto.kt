package com.ikemba.inventrar.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    @SerialName("response_code") var responseCode: Int,
    @SerialName("response_message") val responseMessage: String?,
    )