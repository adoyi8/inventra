package com.sundroid.kobokobo.hotel.data.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.domain.model.BookingStatus
import com.sundroid.kobokobo.hotel.domain.model.Room
import com.sundroid.kobokobo.hotel.domain.repository.HotelRepository
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.firestore.firestore
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import dev.gitlive.firebase.firestore.Direction
import java.util.UUID

class FirebaseHotelRepository : HotelRepository {
    
    // Firestore Collections
    // businesses/{businessId}/rooms
    // businesses/{businessId}/bookings

    private val firestore = Firebase.firestore

    override suspend fun addRoom(room: Room): Result<Room, DataError> {
        return try {
            val businessId = room.businessId
            if (businessId.isBlank()) return Result.Error(DataError.Local.UNKNOWN)

            val collection = firestore.collection("businesses").document(businessId).collection("rooms")
            val newDoc = if (room.id.isBlank()) collection.document(UUID.randomUUID().toString()) else collection.document(room.id)
            val finalRoom = room.copy(id = newDoc.id)
            
            newDoc.set(finalRoom)
            Result.Success(finalRoom)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun updateRoom(room: Room): Result<Room, DataError> {
        return try {
            val businessId = room.businessId
            if (businessId.isBlank()) return Result.Error(DataError.Local.UNKNOWN)

            firestore.collection("businesses").document(businessId)
                .collection("rooms").document(room.id).set(room)
            Result.Success(room)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun deleteRoom(roomId: String, businessId: String): Result<Unit, DataError> {
         return try {
            firestore.collection("businesses").document(businessId)
                .collection("rooms").document(roomId).delete()
            Result.Success(Unit)
        } catch (e: Exception) {
             e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override fun getRooms(businessId: String): Flow<List<Room>> = flow {
        try {
            firestore.collection("businesses").document(businessId)
                .collection("rooms").snapshots.collect { snapshot ->
                    val rooms = snapshot.documents.map { it.data<Room>() }
                    emit(rooms)
                }
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }

    override suspend fun getRoomById(roomId: String, businessId: String): Result<Room, DataError> {
         return try {
            val doc = firestore.collection("businesses").document(businessId)
                .collection("rooms").document(roomId).get()
            val room = doc.data<Room>()
            Result.Success(room)
        } catch (e: Exception) {
             e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun createBooking(booking: Booking): Result<Booking, DataError> {
         return try {
            val businessId = booking.businessId
            if (businessId.isBlank()) return Result.Error(DataError.Local.UNKNOWN)

            val collection = firestore.collection("businesses").document(businessId).collection("bookings")
            val newDoc = if (booking.id.isBlank()) collection.document(UUID.randomUUID().toString()) else collection.document(booking.id)
            val finalBooking = booking.copy(id = newDoc.id)
            
            newDoc.set(finalBooking)
            Result.Success(finalBooking)
        } catch (e: Exception) {
             e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun updateBooking(booking: Booking): Result<Booking, DataError> {
        return try {
            val businessId = booking.businessId
            if (businessId.isBlank()) return Result.Error(DataError.Local.UNKNOWN)

            firestore.collection("businesses").document(businessId)
                .collection("bookings").document(booking.id).set(booking)
            Result.Success(booking)
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override suspend fun checkOut(bookingId: String, businessId: String, actualCheckOutTime: Long): Result<Unit, DataError> {
         return try {
            val docRef = firestore.collection("businesses").document(businessId)
                .collection("bookings").document(bookingId)
            
            // Should probably use transaction but simple update for now
            // Update status and actualCheckOutTime
            // Note: partial update support in gitlive firebase might differ, try merge or read-modify-write
             // Reading first to be safe
             val snap = docRef.get()
             var booking = snap.data<Booking>()
             booking = booking.copy(actualCheckOutTime = actualCheckOutTime, status = BookingStatus.COMPLETED)
             docRef.set(booking)
             
            Result.Success(Unit)
        } catch (e: Exception) {
             e.printStackTrace()
            Result.Error(DataError.Remote.SERVER)
        }
    }

    override fun getActiveBookings(businessId: String): Flow<List<Booking>> = flow {
         try {
             // Query for ACTIVE bookings with real-time updates
             // gitlive firestore query support
            firestore.collection("businesses").document(businessId)
                .collection("bookings")
                .where { "status" equalTo BookingStatus.ACTIVE.name }
                .snapshots.collect { snapshot ->
                    val bookings = snapshot.documents.map { it.data<Booking>() }
                    emit(bookings)
                }
        } catch (e: Exception) {
             e.printStackTrace()
            emit(emptyList())
        }
    }

    override fun getBookingHistory(businessId: String, limit: Int): Flow<List<Booking>> = flow {
        try {
            val snapshot = firestore.collection("businesses").document(businessId)
                .collection("bookings")
                .orderBy("checkInTime", Direction.DESCENDING)
                .limit(limit)
                .get()
            val bookings = snapshot.documents.map { it.data<Booking>() }
            emit(bookings)
        } catch (e: Exception) {
            e.printStackTrace()
            emit(emptyList())
        }
    }
}
