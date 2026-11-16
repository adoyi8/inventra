package com.sundroid.kobokobo.hotel.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.outlined.History
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.dashboard.utils.Util
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.hotel.domain.model.Booking
import com.sundroid.kobokobo.hotel.presentation.HotelEvent
import com.sundroid.kobokobo.hotel.presentation.HotelState
import com.sundroid.kobokobo.hotel.presentation.HotelViewModel
import com.sundroid.kobokobo.login.presentation.UserViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import kotlinx.coroutines.launch

@Composable
fun BookingHistoryScreen(
    viewModel: HotelViewModel,
    state: HotelState,
    userViewModel: UserViewModel
) {
    var showFilterDialog by remember { mutableStateOf(false) }

    val userState by userViewModel.state.collectAsState()
    val requestState = viewModel.state.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()
    val pdfExporter = com.sundroid.kobokobo.utils.rememberPdfExporter()

    LaunchedEffect(Unit) {
        if (!userState.permissions.contains(SystemPermissions.VIEW_HOTEL_BOOKINGS.id)) {
            viewModel.updateStaffId(userState.currentUser?.id!!)
        }
        viewModel.loadBookingHistory()
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.surfaceContainerLowest,
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.padding(16.dp),
                snackbar = { snackbarData ->
                    Snackbar(
                        action = {
                            snackbarData.visuals.actionLabel?.let { actionLabel ->
                                TextButton(onClick = { snackbarData.performAction() }) {
                                    Text(actionLabel)
                                }
                            }
                        }
                    ) {
                        Text(snackbarData.visuals.message)
                    }
                }
            )
        },
        floatingActionButton = {
            Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
                // Download PDF FAB
                androidx.compose.material3.FloatingActionButton(
                    onClick = {
                        val businessName = userViewModel.getUser()?.businessName ?: "Inventra"
                        viewModel.exportBookingHistory(
                            businessName = businessName,
                            pdfExporter = pdfExporter,
                            onResult = { result ->
                                coroutineScope.launch {
                                    val snackResult = snackbarHostState.showSnackbar(
                                        message = result.message,
                                        actionLabel = if (result.openTarget != null) "Open" else null
                                    )
                                    if (snackResult == SnackbarResult.ActionPerformed && result.openTarget != null) {
                                        val openError = pdfExporter.openPdf(result.openTarget)
                                        if (openError != null) {
                                            snackbarHostState.showSnackbar(openError)
                                        }
                                    }
                                }
                            }
                        )
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                    Icon(Icons.Default.Download, contentDescription = "Download PDF")
                }

//                // Refresh FAB
//                androidx.compose.material3.FloatingActionButton(
//                    onClick = { viewModel.onEvent(HotelEvent.RefreshHistory) },
//                    containerColor = MaterialTheme.colorScheme.surfaceVariant,
//                    contentColor = MaterialTheme.colorScheme.onSurfaceVariant
//                ) {
//                    Icon(Icons.Default.Refresh, contentDescription = "Refresh")
//                }

                // Filter FAB
                androidx.compose.material3.FloatingActionButton(
                    onClick = { showFilterDialog = true },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                ) {
                    Icon(Icons.Default.FilterList, contentDescription = "Filter")
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(horizontal = 16.dp)
        ) {
            Spacer(modifier = Modifier.size(16.dp))

            // --- Stats Card ---
            ElevatedCard(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
                colors = CardDefaults.elevatedCardColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Column {
                        Text(
                            text = "Total Revenue (Page)",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.outline
                        )
                        Text(
                            text = Util.currencyFormat(state.pageTotalAmount),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                    
                    Box(
                        modifier = Modifier
                            .background(MaterialTheme.colorScheme.secondaryContainer, RoundedCornerShape(16.dp))
                            .padding(horizontal = 12.dp, vertical = 6.dp)
                    ) {
                        Text(
                            text = "${state.bookingHistory.size} Records Total",
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSecondaryContainer
                        )
                    }
                }
            }

            // --- Booking List & Pagination ---
            val totalPages = (state.bookingHistory.size + state.itemsPerPage - 1) / state.itemsPerPage
            val safeTotalPages = if (totalPages < 1) 1 else totalPages
            
            val start = (state.currentPage - 1) * state.itemsPerPage
            val end = minOf(start + state.itemsPerPage, state.bookingHistory.size)
            // Ensure indices are valid even if list shrunk
            val visibleBookings = if (start < state.bookingHistory.size && start >= 0) {
                 state.bookingHistory.subList(start, end)
            } else {
                 emptyList()
            }

            if (state.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            } else if (state.bookingHistory.isEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.History,
                            contentDescription = null,
                            modifier = Modifier.size(64.dp),
                            tint = MaterialTheme.colorScheme.surfaceVariant
                        )
                        Spacer(modifier = Modifier.size(16.dp))
                        Text(
                            text = "No history found",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                Column(
                    modifier = Modifier.fillMaxSize()
                ) {
                    LazyColumn(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        items(visibleBookings) { booking ->
                            BookingHistoryCard(booking)
                        }
                        
                        item { Spacer(modifier = Modifier.size(80.dp)) } // Spacer for visual padding
                    }
                    
                    PaginationControls(
                        currentPage = state.currentPage,
                        totalPages = safeTotalPages,
                        onPageChange = { viewModel.onEvent(HotelEvent.SetPage(it)) }
                    )
                }
            }
        }
    }

    if (showFilterDialog) {
        BookingHistoryFilterDialog(
            state = state,
            viewModel = viewModel,
            onDismiss = { showFilterDialog = false },
            userViewModel = userViewModel
        )
    }
}

@Composable
fun BookingHistoryCard(booking: Booking) {
    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateString = try { dateFormat.format(Date(booking.checkInTime)) } catch(e: Exception) { "Invalid Date" }

    AnimatedVisibility(
        visible = true,
        enter = slideInVertically(initialOffsetY = { 20 }) + fadeIn()
    ) {
        ElevatedCard(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
            colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
            shape = RoundedCornerShape(12.dp)
        ) {
            Column(modifier = Modifier.padding(12.dp)) {
                // Top Row: Date and Amount
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.CalendarToday,
                            contentDescription = null,
                            modifier = Modifier.size(14.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = dateString,
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    Text(
                        text = Util.currencyFormat(booking.totalCost),
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }

                Spacer(modifier = Modifier.size(8.dp))
                HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.size(8.dp))

                // Middle Row: Room and Customer
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Room", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                        Text(
                            text = booking.roomNumber, 
                            style = MaterialTheme.typography.bodyMedium, 
                            fontWeight = FontWeight.Medium
                        )
                    }
                    Column(modifier = Modifier.weight(1f)) {
                        Text("Customer", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                        Text(
                            text = booking.customerName, 
                            style = MaterialTheme.typography.bodyMedium,
                            fontWeight = FontWeight.Medium,
                            maxLines = 1
                        )
                    }
                }

                // Discount row (only if discount > 0)
                if (booking.discount > 0) {
                    Spacer(modifier = Modifier.size(4.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("Discount", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline, fontSize = 10.sp)
                        Text(
                            text = "- ${Util.currencyFormat(booking.discount)}",
                            style = MaterialTheme.typography.bodySmall,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.error
                        )
                    }
                }

                Spacer(modifier = Modifier.size(8.dp))

                // Bottom Row: Payment and Staff
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(6.dp))
                        .padding(horizontal = 8.dp, vertical = 4.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.secondary, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (booking.isPaid) booking.paymentMethod else "Not paid",
                            style = MaterialTheme.typography.bodySmall,
                            color = if (booking.isPaid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
                        )
                    }
                    
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Staff",
                            modifier = Modifier.size(12.dp),
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = booking.receptionistName.ifBlank { "Unknown" },
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.outline
                        )
                    }
                }
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BookingHistoryFilterDialog(
    state: HotelState,
    viewModel: HotelViewModel,
    userViewModel: UserViewModel,
    onDismiss: () -> Unit
) {
    // Keep logic same, just refine UI slightly if needed
    // Temp State
    var tempStartDate by remember { mutableStateOf(state.filterStartDate) }
    var tempEndDate by remember { mutableStateOf(state.filterEndDate) }
    var tempPaymentMethod by remember { mutableStateOf(state.filterPaymentMethod) }
    var tempStaffId by remember { mutableStateOf(state.filterStaffId) }
    var tempRoomId by remember { mutableStateOf(state.filterRoomId) }
    var tempLimit by remember { mutableStateOf(state.historyLimit) }
    val userState by userViewModel.state.collectAsState()

    // Dropdown States
    var paymentExpanded by remember { mutableStateOf(false) }
    var staffExpanded by remember { mutableStateOf(false) }
    var roomExpanded by remember { mutableStateOf(false) }
    var limitExpanded by remember { mutableStateOf(false) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    val dateRangeText = if (tempStartDate != null && tempEndDate != null) {
        val sdf = SimpleDateFormat("MMM dd", Locale.getDefault())
        "${sdf.format(Date(tempStartDate!!))} - ${sdf.format(Date(tempEndDate!!))}"
    } else {
        "Select Date Range"
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter History", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. Date Range
                Column {
                    Text(
                        "Date Range",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    OutlinedTextField(
                        value = dateRangeText,
                        onValueChange = {},
                        readOnly = true,
                        shape = RoundedCornerShape(12.dp),
                        leadingIcon = {
                            Icon(Icons.Default.CalendarToday, null)
                        },
                        trailingIcon = {
                            if (tempStartDate != null) {
                                IconButton(onClick = {
                                    tempStartDate = null
                                    tempEndDate = null
                                }) {
                                    Icon(Icons.Default.Clear, contentDescription = "Clear")
                                }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().clickable { showDateRangePicker = true }
                    )
                }

                // 2. Staff
                if (userState.permissions.contains(SystemPermissions.VIEW_HOTEL_BOOKINGS.id)) {
                Column {
                    Text(
                        "Staff Member",
                        style = MaterialTheme.typography.labelMedium,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = staffExpanded,
                        onExpandedChange = { staffExpanded = !staffExpanded }
                    ) {
                        var staffName =
                            state.staffList.find { it.userId == tempStaffId }?.name ?: "All Staff"


                        OutlinedTextField(
                            value = if (tempStaffId == null) "All Staff" else staffName,
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = staffExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = staffExpanded,
                            onDismissRequest = { staffExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Staff") },
                                onClick = {
                                    tempStaffId = null
                                    staffExpanded = false
                                }
                            )
                            state.staffList.forEach { staff ->
                                DropdownMenuItem(
                                    text = { Text(staff.name) },
                                    onClick = {
                                        tempStaffId = staff.userId
                                        staffExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }

                // 3. Room
               Column {
                    Text("Room", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.size(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = roomExpanded,
                        onExpandedChange = { roomExpanded = !roomExpanded }
                    ) {
                        val roomNumber = state.rooms.find { it.id == tempRoomId }?.number ?: "All Rooms"
                        OutlinedTextField(
                            value = if (tempRoomId == null) "All Rooms" else "Room $roomNumber",
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = roomExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = roomExpanded,
                            onDismissRequest = { roomExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Rooms") },
                                onClick = {
                                    tempRoomId = null
                                    roomExpanded = false
                                }
                            )
                            state.rooms.forEach { room ->
                                DropdownMenuItem(
                                    text = { Text("Room ${room.number}") },
                                    onClick = {
                                        tempRoomId = room.id
                                        roomExpanded = false
                                    }
                                )
                            }
                        }
                    }
               }

                // 4. Payment Method
                Column {
                    Text("Payment Method", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.size(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = paymentExpanded,
                        onExpandedChange = { paymentExpanded = !paymentExpanded }
                    ) {
                        OutlinedTextField(
                            value = tempPaymentMethod ?: "All Methods",
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = paymentExpanded,
                            onDismissRequest = { paymentExpanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("All Methods") },
                                onClick = {
                                    tempPaymentMethod = null
                                    paymentExpanded = false
                                }
                            )
                            state.paymentMethods.forEach { method ->
                                DropdownMenuItem(
                                    text = { Text(method.name) },
                                    onClick = {
                                        tempPaymentMethod = method.name
                                        paymentExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
                
                // 5. Limit
                Column {
                    Text("Limit Records", style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.primary)
                    Spacer(modifier = Modifier.size(8.dp))
                    ExposedDropdownMenuBox(
                        expanded = limitExpanded,
                        onExpandedChange = { limitExpanded = !limitExpanded }
                    ) {
                        OutlinedTextField(
                            value = tempLimit.toString(),
                            onValueChange = {},
                            readOnly = true,
                            shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = limitExpanded) },
                            modifier = Modifier.fillMaxWidth().menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = limitExpanded,
                            onDismissRequest = { limitExpanded = false }
                        ) {
                            listOf(10, 50, 100, 200, 500).forEach { limit ->
                                DropdownMenuItem(
                                    text = { Text(limit.toString()) },
                                    onClick = {
                                        tempLimit = limit
                                        limitExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                viewModel.onEvent(HotelEvent.SetHistoryDateRange(tempStartDate, tempEndDate))
                viewModel.onEvent(HotelEvent.SetStaffFilter(tempStaffId))
                viewModel.onEvent(HotelEvent.SetRoomFilter(tempRoomId))
                viewModel.onEvent(HotelEvent.SetPaymentMethodFilter(tempPaymentMethod))
                viewModel.onEvent(HotelEvent.SetHistoryLimit(tempLimit))
                onDismiss()
            }) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )

    if (showDateRangePicker) {
        val datePickerState = rememberDateRangePickerState(
            initialSelectedStartDateMillis = tempStartDate,
            initialSelectedEndDateMillis = tempEndDate
        )
        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    tempStartDate = datePickerState.selectedStartDateMillis
                    tempEndDate = datePickerState.selectedEndDateMillis
                    showDateRangePicker = false
                }) { Text("Confirm") }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) { Text("Cancel") }
            }
        ) {
            DateRangePicker(state = datePickerState)
        }
    }
}
@Composable
fun PaginationControls(
    currentPage: Int,
    totalPages: Int,
    onPageChange: (Int) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChange(currentPage - 1) },
            enabled = currentPage > 1,
            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Page $currentPage of $totalPages",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { onPageChange(currentPage + 1) },
            enabled = currentPage < totalPages,
            colors = androidx.compose.material3.IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
        }
    }
}
