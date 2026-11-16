package com.sundroid.kobokobo.hotel.presentation

import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.domain.model.Room

data class HotelState(
    val rooms: List<Room> = emptyList(),
    val activeBookings: List<Booking> = emptyList(),
    val bookingHistory: List<Booking> = emptyList(),
    val isLoading: Boolean = false,
    val error: DataError? = null,
    val showAddRoomDialog: Boolean = false,
    val showCheckInDialog: Boolean = false,
    val showCheckOutDialog: Boolean = false,
    val selectedRoom: Room? = null,
    val selectedBooking: Booking? = null,
    val paymentMethods: List<PaymentMethod> = emptyList(),
    // History & Filters
    val staffList: List<Staff> = emptyList(),
    val historyLimit: Int = 100, // Total items to fetch from server
    val totalHistoryAmount: Double = 0.0, // Total of ALL fetched items
    val filterStartDate: Long? = null,
    val filterEndDate: Long? = null,
    val filterPaymentMethod: String? = null,
    val filterRoomId: String? = null,
    val filterStaffId: String? = null,
    // Pagination
    val currentPage: Int = 1,
    val itemsPerPage: Int = 10,
    val pageTotalAmount: Double = 0.0 // Total of currently displayed page
)
