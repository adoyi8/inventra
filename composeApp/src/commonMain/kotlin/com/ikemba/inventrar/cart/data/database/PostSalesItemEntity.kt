package com.ikemba.inventrar.cart.data.database

import kotlinx.serialization.Serializable

@Serializable
data class PostSalesItemEntity(
    var product_id :Int,
    var product_name :String?,
    var qty: String,
    var subtotal: Double,
    var vat_rate: Double,
    var vat_amount: Double

)