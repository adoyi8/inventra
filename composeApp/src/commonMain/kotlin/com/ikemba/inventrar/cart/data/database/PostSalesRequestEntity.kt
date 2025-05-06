package com.ikemba.inventrar.cart.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey
import kotlinx.serialization.Serializable


@Serializable
@Entity
data class PostSalesRequestEntity(
    var cart: String? = null,
    var total: Double? = null,
    var total_vat: Double? = null,
    var grandTotal: Double? = null,
    var payment_method: String? = null,
    var syncedStatus: Boolean = false,
    @PrimaryKey(autoGenerate = false)
    var reference: String,
)