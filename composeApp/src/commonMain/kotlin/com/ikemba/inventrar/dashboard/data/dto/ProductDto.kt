package com.ikemba.inventrar.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ProductDto(
    @SerialName("category")
    val categoryDto: List<CategoryDto>? = null,
    @SerialName("item")
    val itemDto: List<ItemDto>
)
