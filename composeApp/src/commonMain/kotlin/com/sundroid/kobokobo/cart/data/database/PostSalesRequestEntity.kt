package com.sundroid.kobokobo.cart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class PostSalesRequestEntity(
    val type: String, // <--- NEW
    val reason: String? = null,
    var cart: String? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var payment_method_text: String? = null,
    var is_paid: Boolean = false,
    var payment_processed_by: String? = null,
    var syncedStatus: Boolean = false,
    @PrimaryKey(autoGenerate = false)
    var reference: String,
    val businessId: String,
    val userId: String,
    val userName: String, // This will be a JSON string of PostSalesItemEntity
    val totalProfit: Double? = null
)
