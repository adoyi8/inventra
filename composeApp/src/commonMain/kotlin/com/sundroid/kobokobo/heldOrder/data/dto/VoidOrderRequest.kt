package com.sundroid.kobokobo.heldOrder.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class VoidOrderRequest(
    val reference: String,
    val businessId: String,
    val userId: String = "SYSTEM"
)