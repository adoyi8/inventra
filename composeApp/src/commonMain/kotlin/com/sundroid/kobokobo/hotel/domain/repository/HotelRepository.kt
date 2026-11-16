package com.sundroid.kobokobo.hotel.domain.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.domain.model.Room
import kotlinx.coroutines.flow.Flow

interface HotelRepository {
    // Room Management
    suspend fun addRoom(room: Room): Result<Room, DataError>
    suspend fun updateRoom(room: Room): Result<Room, DataError>
    suspend fun deleteRoom(roomId: String, businessId: String): Result<Unit, DataError>
    fun getRooms(businessId: String): Flow<List<Room>>
    suspend fun getRoomById(roomId: String, businessId: String): Result<Room, DataError>

    // Booking Management
    suspend fun createBooking(booking: Booking): Result<Booking, DataError>
    suspend fun updateBooking(booking: Booking): Result<Booking, DataError>
    suspend fun checkOut(bookingId: String, businessId: String, actualCheckOutTime: Long): Result<Unit, DataError>
    fun getActiveBookings(businessId: String): Flow<List<Booking>>
    fun getBookingHistory(businessId: String, limit: Int = 100): Flow<List<Booking>> // Can implement filters later
}
