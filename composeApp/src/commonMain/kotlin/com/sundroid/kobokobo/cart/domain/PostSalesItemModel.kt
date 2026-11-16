package com.sundroid.kobokobo.cart.domain

import kotlinx.serialization.Serializable

@Serializable
data class PostSalesItemModel(
    var product_id :String,
    var product_name :String?,
    var qty: String,
    var subtotal: Double,
    var vat_rate: Double,
    var vat_amount: Double,
    var discount: String? = "0.0",
    val costPrice: Double? = null, // <--- NEW
    val profit: Double? = null

)