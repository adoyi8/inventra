package com.ikemba.inventrar.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CategoryDto(
    @SerialName("sub_id") val categoryId: String,
    @SerialName("sub_name") val categoryName: String,
    @SerialName("sub_code") val categoryCode: String,
)
