package com.ikemba.inventrar.cart.domain

import kotlinx.serialization.Serializable

@Serializable
data class PostSalesItemModel(
    var product_id :Int,
    var product_name :String?,
    var qty: String,
    var subtotal: Double,
    var vat_rate: Double,
    var vat_amount: Double

)