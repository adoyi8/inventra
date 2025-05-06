package com.ikemba.inventrar.heldOrder.data.dto

import kotlinx.serialization.Serializable


@Serializable
data class VoidOrderRequest(
    val reference: String
)