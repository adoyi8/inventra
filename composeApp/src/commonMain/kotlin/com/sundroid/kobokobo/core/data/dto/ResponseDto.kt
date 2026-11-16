package com.sundroid.kobokobo.core.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseDto(
    @SerialName("responseCode") var responseCode: Int,
    @SerialName("responseMessage") val responseMessage: String?,
    )