package com.sundroid.kobokobo.dashboard.data.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ItemDto(

    @SerialName("id")  val id: String,
    @SerialName("name")  val name: String,
    @SerialName("unit_price")  val unitPrice: String,
    @SerialName("cost_price") val costPrice: String = "0.0",
    @SerialName("image")  val image: String?,
    @SerialName("category_id")  val categoryId: String,
    @SerialName("discount")  val discount: String,
    @SerialName("vat")  val vat: String,
    @SerialName("sku")  val sku: String,
    @SerialName("stock") val stock: String? = "0.0",
    @SerialName("sort_order") val sortOrder: String? = "0"


    )