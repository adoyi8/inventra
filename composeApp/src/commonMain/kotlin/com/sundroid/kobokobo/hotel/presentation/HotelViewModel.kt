package com.sundroid.kobokobo.hotel.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.domain.model.Room
import com.sundroid.kobokobo.hotel.domain.model.RoomStatus
import com.sundroid.kobokobo.hotel.domain.model.StayCategory
import com.sundroid.kobokobo.hotel.domain.repository.HotelRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.util.Calendar

class HotelViewModel(
    private val hotelRepository: HotelRepository,
    private val businessRepository: BusinessRepository,
    private val businessId: String,
    private val staffId: String // Current logged-in user
) : ViewModel() {

    private val _state = MutableStateFlow(HotelState())
    val state = _state.asStateFlow()

    init {
        loadRooms()
        loadActiveBookings()
        loadPaymentMethods()
        loadStaffList() // Load staff for filtering
        setDefaultDateRange() // Set default filter to Today
    }

    private fun setDefaultDateRange() {
        val calendar = Calendar.getInstance()
        
        // Start of Day
        calendar.set(Calendar.HOUR_OF_DAY, 0)
        calendar.set(Calendar.MINUTE, 0)
        calendar.set(Calendar.SECOND, 0)
        calendar.set(Calendar.MILLISECOND, 0)
        val startOfDay = calendar.timeInMillis

        // End of Day
        calendar.set(Calendar.HOUR_OF_DAY, 23)
        calendar.set(Calendar.MINUTE, 59)
        calendar.set(Calendar.SECOND, 59)
        calendar.set(Calendar.MILLISECOND, 999)
        val endOfDay = calendar.timeInMillis

        _state.update { it.copy(filterStartDate = startOfDay, filterEndDate = endOfDay) }
    }
    
    private fun loadStaffList() {
        viewModelScope.launch {
            businessRepository.getStaffForBusiness(businessId)
                .onSuccess { staff ->
                     _state.update { it.copy(staffList = staff) }
                }
        }
    }

    private fun loadPaymentMethods() {
        viewModelScope.launch {
            businessRepository.getPaymentMethods(businessId)
                .onSuccess { methods ->
                    // Ensure "Cash" is always an option
                    val methodsWithCash = if (methods.none { it.name.equals("Cash", ignoreCase = true) }) {
                        methods + PaymentMethod(id = "default_cash", name = "Cash", businessId = businessId, active = true)
                    } else {
                        methods
                    }
                    _state.update { it.copy(paymentMethods = methodsWithCash) }
                }
                .onFailure {
                    // Fallback to Cash on error
                    val defaultMethods = listOf(PaymentMethod(id = "default_cash", name = "Cash", businessId = businessId, active = true))
                    _state.update { it.copy(paymentMethods = defaultMethods) }
                }
        }
    }

    private fun loadRooms() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            hotelRepository.getRooms(businessId).collect { rooms ->
                _state.update { it.copy(rooms = rooms, isLoading = false) }
            }
        }
    }

    private fun loadActiveBookings() {
        viewModelScope.launch {
            hotelRepository.getActiveBookings(businessId).collect { bookings ->
                _state.update { it.copy(activeBookings = bookings) }
            }
        }
    }
    
    fun loadBookingHistory() {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val limit = state.value.historyLimit
            hotelRepository.getBookingHistory(businessId, limit).collect { bookings ->
                // Apply optional client-side filters if needed, or just display raw limit
                val filtered = filterBookings(bookings)
                val totalAmount = filtered.sumOf { it.totalCost }
                _state.update { it.copy(bookingHistory = filtered, totalHistoryAmount = totalAmount, isLoading = false) }
                updatePaginationAfterLoad() // Trigger page calculation
            }
        }
    }
    fun updateStaffId(staffId: String){
        viewModelScope.launch {
            _state.update { it.copy(filterStaffId = staffId) }
        }
    }
    private fun filterBookings(bookings: List<Booking>): List<Booking> {
         // Implement client-side filtering logic here using state.value.filter...
         val state = state.value
         return bookings.filter { booking ->
             val matchesDate = if (state.filterStartDate != null && state.filterEndDate != null) {
                 booking.checkInTime >= state.filterStartDate && booking.checkInTime <= state.filterEndDate
             } else true
             
             val matchesPayment = state.filterPaymentMethod?.let {
                 booking.paymentMethod.equals(it, ignoreCase = true)
             } ?: true
             
             val matchesRoom = state.filterRoomId?.let {
                 booking.roomId == it
             } ?: true
             
             val matchesStaff = state.filterStaffId?.let {
                 booking.receptionistId == it
             } ?: true
             
             matchesDate && matchesPayment && matchesRoom && matchesStaff
         }
    }

    fun onEvent(event: HotelEvent) {
        when (event) {
            is HotelEvent.AddRoom -> addRoom(event.room)
            is HotelEvent.UpdateRoom -> updateRoom(event.room)
            is HotelEvent.DeleteRoom -> deleteRoom(event.roomId)
            is HotelEvent.CheckIn -> checkIn(event.roomId, event.customerName, event.category, event.paymentMethod, event.isPaid, event.numberOfNights, event.discount, event.checkInTime)
            is HotelEvent.CheckOut -> checkOut(event.booking)
            is HotelEvent.CheckOut -> checkOut(event.booking)
            is HotelEvent.SelectRoom -> _state.update { it.copy(selectedRoom = event.room) }
            is HotelEvent.SelectBooking -> _state.update { it.copy(selectedBooking = event.booking) }
            is HotelEvent.ToggleRoomStatus -> toggleRoomStatus(event.room)
            is HotelEvent.ShowAddRoomDialog -> _state.update { it.copy(showAddRoomDialog = event.show) }
            is HotelEvent.ShowCheckInDialog -> _state.update { it.copy(showCheckInDialog = event.show) }
            is HotelEvent.ShowCheckOutDialog -> _state.update { it.copy(showCheckOutDialog = event.show) }
            is HotelEvent.DismissError -> _state.update { it.copy(error = null) }
            is HotelEvent.SetHistoryDateRange -> {
                _state.update { it.copy(filterStartDate = event.startDate, filterEndDate = event.endDate) }
                loadBookingHistory()
            }
            is HotelEvent.SetPaymentMethodFilter -> {
                _state.update { it.copy(filterPaymentMethod = event.method) }
                loadBookingHistory() 
            }
            is HotelEvent.SetRoomFilter -> {
                _state.update { it.copy(filterRoomId = event.roomId) }
                loadBookingHistory()
            }
            is HotelEvent.SetStaffFilter -> {
                _state.update { it.copy(filterStaffId = event.staffId) }
                loadBookingHistory()
            }
            is HotelEvent.SetHistoryLimit -> {
                _state.update { it.copy(historyLimit = event.limit) }
                loadBookingHistory()
            }
            is HotelEvent.RefreshHistory -> loadBookingHistory()
            is HotelEvent.SetPage -> {
                 _state.update { it.copy(currentPage = event.page) }
                 calculatePageData() // Recalculate visible items/total
            }
        }
    }
// ...

    private fun calculatePageData() {
        val allHistory = state.value.bookingHistory // This contains ALL filtered items fetched
        val page = state.value.currentPage
        val perPage = state.value.itemsPerPage
        
        // Calculate start/end indices
        // e.g. Page 1: 0..9 (take 10)
        // e.g. Page 2: 10..19
        val fromIndex = (page - 1) * perPage
        // Ensure fromIndex is valid
        if (fromIndex >= allHistory.size) {
             _state.update { it.copy(pageTotalAmount = 0.0) }
             return
        }
        
        val toIndex = minOf(fromIndex + perPage, allHistory.size)
        val pageItems = allHistory.subList(fromIndex, toIndex)
        val pageTotal = pageItems.sumOf { it.totalCost }
        
        _state.update { it.copy(pageTotalAmount = pageTotal) }
    }

    // Helper to be called whenever history list changes (e.g. initial load or filter change)
    private fun updatePaginationAfterLoad() {
        // Reset to page 1 ?? Or keep current page? Usually reset to 1 on filter/load change
        _state.update { it.copy(currentPage = 1) }
        calculatePageData()
    }

    private fun addRoom(room: Room) {
// ...
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val roomWithBusiness = room.copy(businessId = businessId)
            when (val result = hotelRepository.addRoom(roomWithBusiness)) {
                is Result.Success -> {
                    _state.update { it.copy(isLoading = false, showAddRoomDialog = false) }
                    // Flow updates list automatically
                }
                is Result.Error -> _state.update { it.copy(isLoading = false, error = result.error) }
            }
        }
    }

    private fun updateRoom(room: Room) {
        viewModelScope.launch {
            when (val result = hotelRepository.updateRoom(room)) {
                is Result.Success -> { /* automated flow update */ }
                 is Result.Error -> _state.update { it.copy(error = result.error) }
            }
        }
    }

    private fun deleteRoom(roomId: String) {
        viewModelScope.launch {
            hotelRepository.deleteRoom(roomId, businessId)
        }
    }

    private fun checkIn(roomId: String, customerName: String, category: StayCategory, paymentMethod: String, isPaid: Boolean, numberOfNights: Int, discount: Double, checkInTime: Long) {
        viewModelScope.launch {
            _state.update { it.copy(isLoading = true) }
            val room = state.value.rooms.find { it.id == roomId } ?: return@launch
            val now = checkInTime
            val expectedCheckOut = if (category == StayCategory.SHORT_TIME) {
                now + (1 * 60 * 60 * 1000L) // 1 hour after check in
            } else {
                // Next 12 noon after the check-in time
                val calendar = Calendar.getInstance()
                calendar.timeInMillis = now
                // If check-in is before noon today, checkout is noon today + (nights - 1) days
                // If check-in is at or after noon, checkout is noon tomorrow + (nights - 1) days
                val checkInHour = calendar.get(Calendar.HOUR_OF_DAY)
                if (checkInHour >= 12) {
                    calendar.add(Calendar.DAY_OF_YEAR, 1)
                }
                // Add remaining nights (first night's checkout is the next noon)
                calendar.add(Calendar.DAY_OF_YEAR, numberOfNights - 1)
                calendar.set(Calendar.HOUR_OF_DAY, 12)
                calendar.set(Calendar.MINUTE, 0)
                calendar.set(Calendar.SECOND, 0)
                calendar.set(Calendar.MILLISECOND, 0)
                calendar.timeInMillis
            }
            val baseCost = if (category == StayCategory.SHORT_TIME) room.priceShortTime else room.priceNormalStay
            val cost = (baseCost * numberOfNights) - discount

            val booking = Booking(
                roomId = roomId,
                roomNumber = room.number,
                customerName = customerName,
                stayCategory = category,
                checkInTime = now,
                expectedCheckOutTime = expectedCheckOut,
                totalCost = cost,
                businessId = businessId,
                receptionistId = staffId,
                receptionistName = "Current User", // Fetch real name if possible or update later
                paymentMethod = paymentMethod,
                isPaid = isPaid,
                numberOfNights = numberOfNights,
                discount = discount
            )

            // 1. Create Booking
            val bookingResult = hotelRepository.createBooking(booking)
            if (bookingResult is Result.Success) {
                // 2. Update Room Status to OCCUPIED
                val updatedRoom = room.copy(status = RoomStatus.OCCUPIED)
                hotelRepository.updateRoom(updatedRoom)
                _state.update { it.copy(showCheckInDialog = false, selectedRoom = null, isLoading = false) }
            } else if (bookingResult is Result.Error) {
                 _state.update { it.copy(error = bookingResult.error, isLoading = false) }
            }
        }
    }

    private fun checkOut(booking: Booking) {
        viewModelScope.launch {
            val now = System.currentTimeMillis()
            
            // 1. Mark Booking as Completed
            hotelRepository.checkOut(booking.id, businessId, now)

            // 2. Mark Room as DIRTY or NEEDS_MAINTENANCE upon checkout? UI said "Dirty"
            val room = state.value.rooms.find { it.id == booking.roomId }
            if (room != null) {
                val updatedRoom = room.copy(status = RoomStatus.DIRTY)
                hotelRepository.updateRoom(updatedRoom)
            }
            
            _state.update { it.copy(showCheckOutDialog = false, selectedBooking = null) }
        }
    }
    
    private fun toggleRoomStatus(room: Room) {
        // Simple toggle for manual overrides (e.g. Dirty -> Available)
        viewModelScope.launch {
            val newStatus = when(room.status) {
                RoomStatus.DIRTY -> RoomStatus.AVAILABLE
                RoomStatus.MAINTENANCE -> RoomStatus.AVAILABLE
                RoomStatus.AVAILABLE -> RoomStatus.MAINTENANCE // Maybe allow setting to maintenance
                else -> room.status
            }
            if (newStatus != room.status) {
                hotelRepository.updateRoom(room.copy(status = newStatus))
            }
        }
    }

    fun exportBookingHistory(
        businessName: String,
        pdfExporter: com.sundroid.kobokobo.utils.PdfExporter,
        onResult: (com.sundroid.kobokobo.utils.PdfExportResult) -> Unit
    ) {
        viewModelScope.launch {
            val bookings = state.value.bookingHistory
            if (bookings.isEmpty()) {
                onResult(com.sundroid.kobokobo.utils.PdfExportResult(message = "No bookings to export"))
                return@launch
            }

            val sdf = java.text.SimpleDateFormat("MMM dd, yyyy", java.util.Locale.getDefault())
            val dateRange = if (state.value.filterStartDate != null && state.value.filterEndDate != null) {
                "${sdf.format(java.util.Date(state.value.filterStartDate!!))} - ${sdf.format(java.util.Date(state.value.filterEndDate!!))}"
            } else {
                "All Time"
            }

            val staffName = state.value.staffList.find { it.userId == state.value.filterStaffId }?.name ?: "All"
            val roomFilter = state.value.rooms.find { it.id == state.value.filterRoomId }?.number ?: "All"
            val paymentMethod = state.value.filterPaymentMethod ?: "All"

            val result = pdfExporter.exportBookingHistoryPdf(
                businessName = businessName,
                dateRange = dateRange,
                staffName = staffName,
                roomFilter = roomFilter,
                paymentMethod = paymentMethod,
                bookings = bookings
            )
            onResult(result)
        }
    }
}

sealed interface HotelEvent {
    data class AddRoom(val room: Room) : HotelEvent
    data class UpdateRoom(val room: Room) : HotelEvent
    data class DeleteRoom(val roomId: String) : HotelEvent
    data class CheckIn(val roomId: String, val customerName: String, val category: StayCategory, val paymentMethod: String, val isPaid: Boolean, val numberOfNights: Int, val discount: Double, val checkInTime: Long) : HotelEvent
    data class CheckOut(val booking: Booking) : HotelEvent
    data class SelectRoom(val room: Room) : HotelEvent
    data class SelectBooking(val booking: Booking) : HotelEvent
    data class ToggleRoomStatus(val room: Room) : HotelEvent
    data class ShowAddRoomDialog(val show: Boolean) : HotelEvent
    data class ShowCheckInDialog(val show: Boolean) : HotelEvent
    data class ShowCheckOutDialog(val show: Boolean) : HotelEvent
    object DismissError : HotelEvent
    
    // History specific events
    data class SetHistoryDateRange(val startDate: Long?, val endDate: Long?) : HotelEvent
    data class SetPaymentMethodFilter(val method: String?) : HotelEvent
    data class SetRoomFilter(val roomId: String?) : HotelEvent
    data class SetStaffFilter(val staffId: String?) : HotelEvent
    data class SetHistoryLimit(val limit: Int) : HotelEvent
    data class SetPage(val page: Int) : HotelEvent
    object RefreshHistory : HotelEvent
}
