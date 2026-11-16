package com.sundroid.kobokobo.inventory.data.domain.model

import java.util.UUID

data class Location(
    val id: String = UUID.randomUUID().toString(),
    val name: String,
    val description: String = "",
    val businessId: String,
    val locationType: LocationType = LocationType.STORE,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null,
    val updatedBy: String? = null
)

enum class LocationType {
    STORE,
    SHOP_FRONT,
    BAR,
    WAREHOUSE,
    OTHER
}









