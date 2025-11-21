package com.ikemba.inventrar.cart.domain

import com.ikemba.inventrar.business.data.database.Business
import kotlinx.serialization.Serializable
import java.time.LocalDateTime


@Serializable
data class PostSalesModel(
    var cart: List<PostSalesItemModel>? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var reference: String,
    val created: String = LocalDateTime.now().toString(),
    val businessId: String,
    val userId: String
)
