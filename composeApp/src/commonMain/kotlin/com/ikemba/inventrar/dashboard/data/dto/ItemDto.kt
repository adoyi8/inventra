package com.ikemba.inventrar.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(

    @SerialName("id")  val id: String,
    @SerialName("name")  val name: String,
    @SerialName("unit_price")  val unitPrice: String,
    @SerialName("image")  val image: String?,
    @SerialName("category_id")  val categoryId: String,
    @SerialName("discount")  val discount: String,
    @SerialName("vat")  val vat: String

    )