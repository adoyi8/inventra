package com.sundroid.kobokobo.hotel.domain.model

import kotlinx.serialization.Serializable

@Serializable
enum class BookingStatus {
    ACTIVE,
    COMPLETED,
    CANCELLED
}

@Serializable
data class Booking(
    val id: String = "",
    val roomId: String,
    val roomNumber: String,
    val customerName: String,
    val stayCategory: StayCategory,
    val checkInTime: Long,
    val expectedCheckOutTime: Long,
    val actualCheckOutTime: Long? = null,
    val totalCost: Double,
    val status: BookingStatus = BookingStatus.ACTIVE,
    val receptionistId: String = "",
    val businessId: String = "",
    val receptionistName: String = "",
    val paymentMethod: String = "Cash",
    // Bookings created before payment tracking are treated as paid for compatibility.
    val isPaid: Boolean = true,
    val numberOfNights: Int = 1,
    val discount: Double = 0.0
)
