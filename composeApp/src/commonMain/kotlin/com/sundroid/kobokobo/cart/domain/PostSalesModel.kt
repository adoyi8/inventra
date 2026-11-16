package com.sundroid.kobokobo.cart.domain

import com.sundroid.kobokobo.business.data.database.Business
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class PostSalesModel(
    val type: String, // <--- NEW
    val reason: String? = null,
    var cart: List<PostSalesItemModel>? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var payment_method_text: String? = null,
    var is_paid: Boolean = false,
    var payment_processed_by: String? = null,
    var reference: String,
    val created: String = LocalDateTime.now().toString(),
    val businessId: String,
    val userId: String,
    val userName: String,
    // This will be a list of PostSalesItemModel
    val totalProfit: Double? = null
)
