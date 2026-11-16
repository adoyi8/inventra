package com.sundroid.kobokobo.cart.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class PostSalesItem(
    var product_id :String,
    var product_name :String?,
    var qty: String,
    var subtotal: Double,
    var vat_rate: Double,
    var vat_amount: Double,
    var discount: String? = "0.0",
    val costPrice: Double? = null, // <--- NEW: Cost price of this item
    val profit: Double? = null

)