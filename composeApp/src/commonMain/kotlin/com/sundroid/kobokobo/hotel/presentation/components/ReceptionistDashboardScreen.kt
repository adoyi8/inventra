package com.sundroid.kobokobo.hotel.presentation.components



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.TimePicker
import androidx.compose.material3.rememberTimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.domain.model.Room
import com.sundroid.kobokobo.hotel.domain.model.RoomStatus
import com.sundroid.kobokobo.hotel.domain.model.StayCategory
import com.sundroid.kobokobo.hotel.presentation.HotelEvent
import com.sundroid.kobokobo.hotel.presentation.HotelState
import com.sundroid.kobokobo.hotel.presentation.HotelViewModel

@Composable
fun ReceptionistDashboardScreen(
    viewModel: HotelViewModel,
    state: HotelState
) {
    Box(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        Column {
            var expanded by remember { mutableStateOf(false) }
            var selectedFilter by remember { mutableStateOf("All") }
            val filterOptions = listOf("All", "Available", "Occupied", "Dirty", "Due for checkout")

            val filteredRooms = remember(state.rooms, state.activeBookings, selectedFilter) {
                when (selectedFilter) {
                    "Available" -> state.rooms.filter { it.status == RoomStatus.AVAILABLE }
                    "Occupied" -> state.rooms.filter { it.status == RoomStatus.OCCUPIED }
                    "Dirty" -> state.rooms.filter { it.status == RoomStatus.DIRTY }
                    "Due for checkout" -> state.rooms.filter { room ->
                        if (room.status == RoomStatus.OVERSTAYED) return@filter true
                        val booking = state.activeBookings.find { it.roomId == room.id }
                        booking != null && booking.expectedCheckOutTime <= System.currentTimeMillis()
                    }
                    else -> state.rooms
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {

                Box {
                    Button(onClick = { expanded = true }) {
                        Text(if (selectedFilter == "All") "Filter Rooms" else selectedFilter)
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        filterOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedFilter = option
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(filteredRooms) { room ->
                    RoomCard(room = room, onClick = {
                        viewModel.onEvent(HotelEvent.SelectRoom(room))
                        if (room.status == RoomStatus.AVAILABLE) {
                            viewModel.onEvent(HotelEvent.ShowCheckInDialog(true))
                        } else if (room.status == RoomStatus.OCCUPIED || room.status == RoomStatus.OVERSTAYED) {
                             // Find associated booking?
                             // Logic: If occupied, we probably want to checkout. 
                             // But we need the booking object. ViewModel should handle logic of "Find active booking for this room"
                             // For now, let's assume ViewModel.selectedBooking is updated or we pass room ID to CheckOut event
                             val booking = state.activeBookings.find { it.roomId == room.id }
                             if (booking != null) {
                                 viewModel.onEvent(HotelEvent.SelectBooking(booking))
                                 viewModel.onEvent(HotelEvent.ShowCheckOutDialog(true))
                             }
                        } else {
                            viewModel.onEvent(HotelEvent.ToggleRoomStatus(room))
                        }
                    })
                }
            }
        }
        
        if (state.showCheckInDialog && state.selectedRoom != null) {
            CheckInDialog(
                room = state.selectedRoom!!,
                paymentMethods = state.paymentMethods,
                isLoading = state.isLoading,
                onDismiss = { viewModel.onEvent(HotelEvent.ShowCheckInDialog(false)) },
                onConfirm = { name, category, paymentMethod, isPaid, nights, discount, checkInTime ->
                    viewModel.onEvent(HotelEvent.CheckIn(state.selectedRoom!!.id, name, category, paymentMethod, isPaid, nights, discount, checkInTime))
                }
            )
        }

        if (state.showCheckOutDialog && state.selectedBooking != null) {
             CheckOutDialog(
                 booking = state.selectedBooking!!,
                 onDismiss = { viewModel.onEvent(HotelEvent.ShowCheckOutDialog(false)) },
                 onConfirm = {
                     viewModel.onEvent(HotelEvent.CheckOut(state.selectedBooking!!))
                 }
             )
        }
    }
}

@Composable
fun RoomCard(room: Room, onClick: () -> Unit) {
    val backgroundColor = when (room.status) {
        RoomStatus.AVAILABLE -> Color.Green.copy(alpha = 0.3f)
        RoomStatus.OCCUPIED -> Color.Red.copy(alpha = 0.3f)
        RoomStatus.DIRTY -> Color.Yellow.copy(alpha = 0.3f)
        RoomStatus.MAINTENANCE -> Color.Gray.copy(alpha = 0.3f)
        RoomStatus.OVERSTAYED -> Color.Red // Solid Red for urgency
    }

    Card(
        modifier = Modifier.height(100.dp).clickable { onClick() },
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(room.number, style = MaterialTheme.typography.titleLarge)
                Text(room.status.name, style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CheckInDialog(
    room: Room,
    paymentMethods: List<PaymentMethod>,
    isLoading: Boolean = false,
    onDismiss: () -> Unit,
    onConfirm: (String, StayCategory, String, Boolean, Int, Double, Long) -> Unit
) {
    var customerName by remember { mutableStateOf("") }
    var selectedCategory by remember { mutableStateOf(StayCategory.NORMAL_STAY) }
    var selectedPaymentMethod by remember { mutableStateOf(if (paymentMethods.isNotEmpty()) paymentMethods.first().name else "Cash") }
    var proceedWithoutPayment by remember { mutableStateOf(false) }
    var numberOfNightsText by remember { mutableStateOf("1") }
    var discountText by remember { mutableStateOf("") }
    var useCustomTime by remember { mutableStateOf(false) }
    var showDatePicker by remember { mutableStateOf(false) }
    var showTimePicker by remember { mutableStateOf(false) }
    var customDateMillis by remember { mutableStateOf<Long?>(null) }
    var customHour by remember { mutableStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.HOUR_OF_DAY)) }
    var customMinute by remember { mutableStateOf(java.util.Calendar.getInstance().get(java.util.Calendar.MINUTE)) }

    val basePrice = if (selectedCategory == StayCategory.SHORT_TIME) room.priceShortTime else room.priceNormalStay
    val nights = numberOfNightsText.toIntOrNull() ?: 1
    val discount = discountText.toDoubleOrNull() ?: 0.0
    val totalAmount = (basePrice * nights) - discount

    // Compute the check-in time to pass
    fun computeCheckInTime(): Long {
        return if (useCustomTime && customDateMillis != null) {
            val cal = java.util.Calendar.getInstance()
            cal.timeInMillis = customDateMillis!!
            cal.set(java.util.Calendar.HOUR_OF_DAY, customHour)
            cal.set(java.util.Calendar.MINUTE, customMinute)
            cal.set(java.util.Calendar.SECOND, 0)
            cal.set(java.util.Calendar.MILLISECOND, 0)
            cal.timeInMillis
        } else {
            System.currentTimeMillis()
        }
    }

    // Format the selected date+time for display
    val formattedDateTime = if (useCustomTime && customDateMillis != null) {
        val cal = java.util.Calendar.getInstance()
        cal.timeInMillis = customDateMillis!!
        cal.set(java.util.Calendar.HOUR_OF_DAY, customHour)
        cal.set(java.util.Calendar.MINUTE, customMinute)
        val formatter = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", java.util.Locale.getDefault())
        formatter.format(cal.time)
    } else {
        ""
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Check In - ${room.number}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(
                    value = customerName,
                    onValueChange = { customerName = it },
                    label = { Text("Customer Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Text("Select Stay Category:")
                Column(modifier = Modifier.fillMaxWidth()) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedCategory == StayCategory.NORMAL_STAY,
                            onClick = { selectedCategory = StayCategory.NORMAL_STAY }
                        )
                        Text("Normal Stay - ${room.priceNormalStay}")
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        RadioButton(
                            selected = selectedCategory == StayCategory.SHORT_TIME,
                            onClick = { selectedCategory = StayCategory.SHORT_TIME }
                        )
                        Text("Short Time - ${room.priceShortTime}")
                    }


                }

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = numberOfNightsText,
                        onValueChange = { numberOfNightsText = it },
                        label = { Text("Nights") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                    OutlinedTextField(
                        value = discountText,
                        onValueChange = { discountText = it },
                        label = { Text("Discount") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.weight(1f)
                    )
                }

                // --- Check-in Time Selection ---
                Spacer(modifier = Modifier.height(4.dp))
                Text("Check-in Time:", style = MaterialTheme.typography.titleSmall, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Checkbox(
                        checked = useCustomTime,
                        onCheckedChange = { useCustomTime = it }
                    )
                    Text("Select custom check-in time")
                }

                if (useCustomTime) {
                    OutlinedTextField(
                        value = formattedDateTime,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Check-in Date & Time") },
                        modifier = Modifier.fillMaxWidth().clickable { showDatePicker = true },
                        enabled = false,
                        colors = androidx.compose.material3.OutlinedTextFieldDefaults.colors(
                            disabledTextColor = MaterialTheme.colorScheme.onSurface,
                            disabledBorderColor = MaterialTheme.colorScheme.outline,
                            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                } else {
                    Text("Using current time", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }

                Spacer(modifier = Modifier.height(8.dp))
                Text("Payment Method:")
                LazyRow(modifier = Modifier.fillMaxWidth()) {
                    items(paymentMethods) { method ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .clickable {
                                    selectedPaymentMethod = method.name
                                    proceedWithoutPayment = false
                                }
                                .padding(vertical = 4.dp, horizontal = 8.dp)
                        ) {
                            RadioButton(
                                selected = selectedPaymentMethod == method.name,
                                onClick = {
                                    selectedPaymentMethod = method.name
                                    proceedWithoutPayment = false
                                }
                            )
                            Text(text = method.name, modifier = Modifier.padding(start = 4.dp))
                        }
                    }
                    if (paymentMethods.isEmpty()) {
                        item {
                            Text("No payment methods available. Defaulting to Cash.", modifier = Modifier.padding(8.dp))
                        }
                    }
                }

                Row(
                    modifier = Modifier.fillMaxWidth().clickable {
                        proceedWithoutPayment = !proceedWithoutPayment
                        if (proceedWithoutPayment) selectedPaymentMethod = ""
                    },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = proceedWithoutPayment,
                        onCheckedChange = { checked ->
                            proceedWithoutPayment = checked
                            if (checked) selectedPaymentMethod = ""
                        }
                    )
                    Text("Proceed without payment")
                }
                
                Spacer(modifier = Modifier.height(8.dp))
                Text("Total Amount: $totalAmount", style = MaterialTheme.typography.titleMedium)
            }
        },
        confirmButton = {
            Button(
                onClick = { onConfirm(customerName, selectedCategory, selectedPaymentMethod, !proceedWithoutPayment && selectedPaymentMethod.isNotBlank(), nights, discount, computeCheckInTime()) },
                enabled = !isLoading
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(20.dp), strokeWidth = 2.dp)
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text("Check In")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss, enabled = !isLoading) { Text("Cancel") }
        }
    )

    // Date Picker Dialog - picks the date first, then shows Time Picker
    if (showDatePicker) {
        val datePickerState = androidx.compose.material3.rememberDatePickerState(
            initialSelectedDateMillis = customDateMillis ?: System.currentTimeMillis()
        )
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDatePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        customDateMillis = datePickerState.selectedDateMillis
                        showDatePicker = false
                        showTimePicker = true
                    },
                    enabled = datePickerState.selectedDateMillis != null
                ) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showDatePicker = false }) { Text("Cancel") }
            }
        ) {
            androidx.compose.material3.DatePicker(state = datePickerState)
        }
    }

    // Time Picker Dialog - shown after date is selected
    if (showTimePicker) {
        val timePickerState = rememberTimePickerState(
            initialHour = customHour,
            initialMinute = customMinute
        )
        AlertDialog(
            modifier = Modifier
                .fillMaxWidth()
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(size = 12.dp)
                ),
            onDismissRequest = { showTimePicker = false }
        ) {
            Column(
                modifier = Modifier
                    .background(color = MaterialTheme.colorScheme.surface)
                    .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text("Select Check-in Time", modifier = Modifier.padding(bottom = 16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = androidx.compose.ui.text.font.FontWeight.Bold)
                TimePicker(state = timePickerState)
                Row(
                    modifier = Modifier
                        .padding(top = 16.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    TextButton(onClick = { showTimePicker = false }) { Text("Cancel") }
                    Spacer(modifier = Modifier.width(8.dp))
                    TextButton(onClick = {
                        customHour = timePickerState.hour
                        customMinute = timePickerState.minute
                        showTimePicker = false
                    }) { Text("OK") }
                }
            }
        }
    }
}

@Composable
fun CheckOutDialog(booking: Booking, onDismiss: () -> Unit, onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { 
            Text(
                "Checkout - Room ${booking.roomNumber}", 
                style = MaterialTheme.typography.headlineSmall,
                fontWeight = androidx.compose.ui.text.font.FontWeight.Bold
            ) 
        },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                val now = System.currentTimeMillis()
                val durationMillis = now - booking.checkInTime
                val durationHours = durationMillis / (1000 * 60 * 60)
                val durationMinutes = (durationMillis / (1000 * 60)) % 60
                
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy hh:mm a", java.util.Locale.getDefault())
                val checkInStr = dateFormat.format(java.util.Date(booking.checkInTime))
                val dueStr = dateFormat.format(java.util.Date(booking.expectedCheckOutTime))
                
                Card(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        DetailRow("Customer", booking.customerName)
                        DetailRow("Category", booking.stayCategory.name.replace("_", " "))
                        androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailRow("Check-in Time", checkInStr)
                        DetailRow("Time Due", dueStr)
                        DetailRow("Duration Stayed", "${durationHours}h ${durationMinutes}m")
                        androidx.compose.material3.HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                        DetailRow("Discount", booking.discount.toString())
                        DetailRow("Total Cost", booking.totalCost.toString(), isTotal = true)
                    }
                }
                
                Spacer(modifier = Modifier.height(16.dp))
                Text(
                    "Are you sure you want to confirm checkout?", 
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        },
        confirmButton = {
            Button(onClick = onConfirm) {
                Text("Confirm Checkout")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}

@Composable
private fun DetailRow(label: String, value: String, isTotal: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = if (isTotal) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium,
            color = if (isTotal) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isTotal) MaterialTheme.typography.titleLarge else MaterialTheme.typography.bodyLarge,
            color = if (isTotal) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface,
            fontWeight = if (isTotal) androidx.compose.ui.text.font.FontWeight.Bold else androidx.compose.ui.text.font.FontWeight.SemiBold
        )
    }
}
