package com.sundroid.kobokobo.cart.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class PostSalesRequest(
    var type: String,
    var reason: String? = null,
    var cart: List<PostSalesItem>? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var reference: String? = null,
    var is_held: Boolean = false,
    var businessId: String,
    var userId: String,
    var userName: String,
    var total_profit: Double? = null,
    var payment_method_text: String? = null,
    var is_paid: Boolean = false,
    var payment_processed_by: String? = null,
)
