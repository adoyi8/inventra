package com.sundroid.kobokobo.hotel.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class RoomStatus {
    AVAILABLE,
    OCCUPIED,
    DIRTY,
    MAINTENANCE,
    OVERSTAYED
}

@Serializable
enum class StayCategory(val label: String, val durationHours: Int) {
    SHORT_TIME("Short Time", 5),
    NORMAL_STAY("Normal Stay", 12)
}

@Serializable
data class Room(
    val id: String = "",
    val number: String,
    val category: StayCategory = StayCategory.NORMAL_STAY, // Default category, though bookings override this
    val status: RoomStatus = RoomStatus.AVAILABLE,
    val priceShortTime: Double = 0.0,
    val priceNormalStay: Double = 0.0,
    val notes: String = "",
    val businessId: String = "" 
)
