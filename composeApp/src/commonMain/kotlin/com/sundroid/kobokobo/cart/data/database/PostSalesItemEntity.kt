package com.sundroid.kobokobo.cart.data.database

import kotlinx.serialization.Serializable

@Serializable
data class PostSalesItemEntity(
    var product_id :String,
    var product_name :String?,
    var qty: String,
    var subtotal: Double,
    var vat_rate: Double,
    val discount: String? = null,
    var vat_amount: Double,
    val costPrice: Double? = null, // <--- NEW
    val profit: Double? = null // <--- NEW

)