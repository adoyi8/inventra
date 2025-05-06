package com.ikemba.inventrar.cart.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class PostSalesRequest(
    var cart: List<PostSalesItem>? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var reference: String? = null,
    var is_held: Boolean = false,
)