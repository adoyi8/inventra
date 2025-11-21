package com.ikemba.inventrar.transactionHistory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.ikemba.inventrar.admin.data.domain.model.SystemPermissions
import com.ikemba.inventrar.business.data.domain.model.Staff
import com.ikemba.inventrar.cart.presentation.components.ReceiptDialog
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.core.presentation.components.ProgressDialog
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.theme.TextSize.bodyMedium
import com.ikemba.inventrar.theme.TextSize.titleMedium
import com.ikemba.inventrar.theme.TextSize.titleSmall
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.transactionHistory.domain.OrderModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import inventrar.composeapp.generated.resources.eye_icon
import inventrar.composeapp.generated.resources.filter
import inventrar.composeapp.generated.resources.filter_list
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

// A simple enum to make our layout choice clear
enum class ScreenSize { COMPACT, EXPANDED }

@Composable
fun TransactionScreen(viewModel: TransactionHistoryViewModel = koinViewModel(), userViewModel: UserViewModel) {
    // State for the new filter dialog
    var showFilterDialog by remember { mutableStateOf(false) }
    val paginationRequest = remember { mutableStateOf(PaginationRequest()) }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()


    // Fetch data on initial composition
    LaunchedEffect(Unit) {
        if(!userState.value.permissions.contains(SystemPermissions.VIEW_EXPENSES.id)){
            paginationRequest.value.staff_user_id = FirebaseAuth.getInstance().currentUser?.uid
        }
        viewModel.getTransactionHistory(paginationRequest.value)
    }

    Scaffold { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            val screenSize = if (maxWidth < 600.dp) {
                ScreenSize.COMPACT
            } else {
                ScreenSize.EXPANDED
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (screenSize == ScreenSize.COMPACT) 16.dp else 24.dp)
            ) {
                // --- Search, Entries, and NEW Filter Button ---
                SearchAndEntryControls(
                    screenSize = screenSize,
                    paginationRequest = paginationRequest,
                    viewModel = viewModel,
                    onFilterClick = { showFilterDialog = true } // This opens the dialog
                )

                Spacer(modifier = Modifier.height(8.dp))
                val pageTransactions = state.value.transactionHistory.orders ?: emptyList()
                PageTotalCard(transactions = pageTransactions)

                // --- Transaction List/Table ---
                TransactionList(
                    screenSize = screenSize,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f) // Fills available space
                )

                Spacer(modifier = Modifier.height(8.dp))

                // --- Pagination ---
                Row(Modifier.fillMaxWidth()) {
                    PaginationControls(
                        screenSize = screenSize,
                        viewModel = viewModel,
                        paginationRequest = paginationRequest,
                        modifier = Modifier.weight(4f)
                    )



                    }
                }


            // --- Modals and Overlays ---

            // Handle empty state
            if (state.value.transactionHistory.orders.isNullOrEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CustomText("There are no items to display") // Using standard Text
                }
            }


            if (showFilterDialog) {
                FilterDialog(
                    currentRequest = paginationRequest.value,
                    viewModel = viewModel, // We still need this for the staff list
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { newRequest ->
                        // 1. Store the new request as the current state
                        paginationRequest.value = newRequest
                        // 2. Tell the ViewModel to fetch with these filters
                        viewModel.applyFilters(newRequest)
                    },
                    userViewModel = userViewModel
                )
            }

            // Progress Dialog
            AnimatedVisibility(visibleState = state.value.showProgressDialog) { // Use 'visible'
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ProgressDialog("Fetching Transaction history")
                }
            }

            // Receipt Dialog
            ReceiptDialog(
                visibleState = state.value.showReceipt, // Use 'show'
                onDismiss = { viewModel.toggleShowReceipt(false) },
                receiptModel = state.value.receiptModel
            )

            // Snackbar
            SnackbarHost(
                hostState = state.value.snackBarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}
@Composable
fun FilterControls(
    screenSize: ScreenSize,
    selectedDateRange: String,
    onDateRangeClick: () -> Unit,
    onDateRangeClear: () -> Unit,
    paginationRequest: MutableState<PaginationRequest>
) {
    val datePickerModifier = Modifier
        .clickable(onClick = onDateRangeClick)

    // Use Column for compact, Row for expanded
    if (screenSize == ScreenSize.COMPACT) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            DropdownMenuSample(
                paginationRequest = paginationRequest,
                modifier = Modifier.fillMaxWidth()
            )
            DatePickerField(
                selectedDateRange = selectedDateRange,
                onDateRangeClick = onDateRangeClick,
                onDateRangeClear = onDateRangeClear,
                modifier = Modifier.fillMaxWidth().then(datePickerModifier)
            )
        }
    } else {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            DropdownMenuSample(
                paginationRequest = paginationRequest,
                modifier = Modifier.width(170.dp) // Keep original width for desktop
            )
            DatePickerField(
                selectedDateRange = selectedDateRange,
                onDateRangeClick = onDateRangeClick,
                onDateRangeClear = onDateRangeClear,
                modifier = datePickerModifier // Let it size naturally
            )
        }
    }
}

// Extracted the DatePickerField for clarity
@Composable
fun DatePickerField(
    selectedDateRange: String,
    onDateRangeClick: () -> Unit,
    onDateRangeClear: () -> Unit,
    modifier: Modifier = Modifier
) {
    OutlinedTextField(
        value = selectedDateRange,
        onValueChange = {},
        readOnly = true,
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = "Select Date Range",
                modifier = Modifier.size(24.dp) // Standard icon size
            )
        },
        trailingIcon = {
            // Only show clear icon if a date is selected
            if (selectedDateRange != "Select Date Range") {
                IconButton(onClick = onDateRangeClear) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear"
                    )
                }
            }
        },
        modifier = modifier.clickable(onClick = {
            onDateRangeClick
            println("eagles")
        })
    )
}

@Composable
fun SearchAndEntryControls(
    screenSize: ScreenSize,
    paginationRequest: MutableState<PaginationRequest>,
    viewModel: TransactionHistoryViewModel,
    onFilterClick: () -> Unit // <-- New parameter
) {
    if (screenSize == ScreenSize.COMPACT) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Search bar and filter icon now in a Row
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchBar(
                    paginationRequest = paginationRequest,
                    modifier = Modifier.weight(1f) // Search bar takes most space
                )
                IconButton(onClick = onFilterClick) {
                    Icon(painter = painterResource(Res.drawable.filter), modifier = Modifier.size(48.dp), contentDescription = "Filter transactions")
                }
            }


        }
    } else {
        // Desktop layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {


            // Search bar and filter icon
            Row(
                modifier = Modifier.width(400.dp), // Widen to fit icon
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SearchBar(
                    paginationRequest = paginationRequest,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFilterClick) {
                    Icon(painter = painterResource(Res.drawable.filter_list), contentDescription = "Filter transactions")
                }
            }
        }
    }
}
@Composable
fun TransactionList(
    screenSize: ScreenSize,
    viewModel: TransactionHistoryViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val transactions = state.value.transactionHistory.orders ?: emptyList()

    // This outer Box ensures the list and scrollbars (on desktop) are contained
    Box(modifier = modifier.fillMaxWidth()) {
        if (screenSize == ScreenSize.COMPACT) {
            // --- COMPACT (Mobile) ---
            // A simple vertical list of cards. This is the most readable
            // pattern on a small screen.
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(transactions) { transaction ->
                    TransactionCardItem(
                        transaction = transaction,
                        onViewReceipt = { viewModel.getReceipt(transaction.orderId) }
                    )
                }
            }
        } else {
            // --- EXPANDED (Desktop) ---
            // Here we use a horizontally scrolling Box with a LazyColumn inside.
            // This preserves the table structure.
            val horizontalScrollState = rememberScrollState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        // Apply the horizontal scroll to the LazyColumn itself
                        .horizontalScroll(horizontalScrollState)
                ) {
                    // 1. Table Header
                    item {
                        TransactionTableHeader()
                    }

                    // 2. Table Rows
                    items(transactions) { transaction ->
                        val index = transactions.indexOf(transaction)
                        val itemNumber = (state.value.transactionHistory.limit * (state.value.transactionHistory.page - 1) + index + 1)
                        TransactionRowItem(
                            itemNumber = itemNumber.toString(),
                            transaction = transaction,
                            onViewReceipt = { viewModel.getReceipt(transaction.orderId) }
                        )
                    }
                }


            }
        }
    }
}

// --- List Item for COMPACT (Mobile) ---
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TransactionCardItem(
    transaction: OrderModel, // Assuming TransactionOrder is your class
    onViewReceipt: () -> Unit
) {
    Card(
        onClick = onViewReceipt,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Order ID
                CustomText(
                    text = "Order: ${transaction.orderId}",
                    size = titleMedium
                )
                // Amount
                CustomText(
                    text = Util.currencyFormat(transaction.totalAmount.toDouble()),
                    size = titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            // Date
            CustomText(
                text = "Date: ${transaction.created}",
                size = bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            // Payment Method
            CustomText(
                text = "Payment: ${transaction.paymentMethod}",
                size = bodyMedium
            )
        }
    }
}

// --- Header for EXPANDED (Desktop) ---
@Composable
fun TransactionTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // We use weights to define columns. This is flexible.
        // Make sure the total width is at least 1300.dp to force scrolling
        // if needed, or just let weights handle it.
        // I'll base weights roughly on your original fixed widths.
        TableCell("No.", modifier = Modifier.width(100.dp))
        TableCell("Order No", modifier = Modifier.width(300.dp))
        TableCell("Date & Amount", modifier = Modifier.width(300.dp))
        TableCell("Payment Method", modifier = Modifier.width(300.dp))
        TableCell("Actions", modifier = Modifier.width(200.dp), alignment = TextAlign.Center)
    }
}

// --- Row Item for EXPANDED (Desktop) ---
@Composable
fun TransactionRowItem(
    itemNumber: String,
    transaction: OrderModel,
    onViewReceipt: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .border(
                BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.2f)),
                RoundedCornerShape(4.dp)
            )
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // These widths MUST match the header widths
        TableCell(itemNumber, modifier = Modifier.width(100.dp))

        TableCell(transaction.orderId, modifier = Modifier.width(300.dp))

        Column(modifier = Modifier.width(300.dp)) {
            CustomText(transaction.created, size = bodyMedium)
            CustomText(
                Util.currencyFormat(transaction.totalAmount.toDouble()),
                fontWeight = FontWeight.Bold,
                size = bodyMedium
            )
        }

        TableCell(transaction.paymentMethod, modifier = Modifier.width(300.dp))

        Box(
            modifier = Modifier.width(200.dp),
            contentAlignment = Alignment.Center
        ) {
            IconButton(onClick = onViewReceipt) {
                Icon(
                    painter = painterResource(Res.drawable.eye_icon),
                    contentDescription = "View Receipt",
                    modifier = Modifier.size(24.dp) // Standard icon size
                )
            }
        }
    }
}

// Your updated TableCell, now with alignment
@Composable
fun TableCell(
    text: String,
    modifier: Modifier = Modifier,
    alignment: TextAlign = TextAlign.Start,
    fontWeight: FontWeight? = null
) {
    CustomText(
        text = text,
        size = bodyMedium,
        modifier = modifier.padding(horizontal = 8.dp), // Add padding
        textAlign = alignment,
        fontWeight = fontWeight?: FontWeight.Normal
    )
}
@Composable
fun PaginationControls(
    screenSize: ScreenSize,
    viewModel: TransactionHistoryViewModel,
    paginationRequest: MutableState<PaginationRequest>,
    modifier: Modifier = Modifier
) {
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val page = state.value.page
    val totalPages = state.value.totalPages

    // Reusable click handler to avoid repetition
    val onPageChange: (Long) -> Unit = { newPage ->
        debounceJob?.cancel()
        viewModel.hideAllDialogs()
        debounceJob = coroutineScope.launch {
            paginationRequest.value.page = newPage
            viewModel.getTransactionHistory(paginationRequest.value)
        }
    }

    if (screenSize == ScreenSize.COMPACT) {
        // --- COMPACT (Mobile) ---
        // Shows "Previous" icon, "Page 1 of 10", "Next" icon
        Row(
            modifier = modifier,
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPageChange((page - 1).toLong()) },
                enabled = page > 1
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
            }

            CustomText(
                text = "Page $page of $totalPages",
                size = bodyMedium
            )

            IconButton(
                onClick = { onPageChange((page + 1).toLong()) },
                enabled = page < totalPages
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
            }
        }
    } else {
        // --- EXPANDED (Desktop) ---
        // Your original button layout
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.Center
        ) {
            PaginationButton(
                onClick = { onPageChange(1) },
                enabled = page > 1,
                text = "First"
            )
            PaginationButton(
                onClick = { onPageChange((page - 1).toLong()) },
                enabled = page > 1,
                text = "Previous"
            )
            // Current page (disabled)
            PaginationButton(
                onClick = {},
                enabled = false,
                text = page.toString()
            )
            PaginationButton(
                onClick = { onPageChange((page + 1).toLong()) },
                enabled = page < totalPages,
                text = "Next"
            )
            PaginationButton(
                onClick = { onPageChange(totalPages.toLong()) },
                enabled = page < totalPages,
                text = "Last"
            )
        }
    }
}

// Your PaginationButton (looks fine, just remove the 0.dp spacer)
@Composable
fun PaginationButton(
    onClick: () -> Unit = {},
    text: String,
    enabled: Boolean,
    modifier: Modifier = Modifier
) {
    Button(
        modifier = modifier,
        onClick = onClick,
        enabled = enabled,
        shape = RectangleShape // Consider RoundedCornerShape(4.dp) for a softer look
    ) {
        CustomText(text = text) // Use standard Text
    }
}
@Composable
fun SearchBar(
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    paginationRequest: MutableState<PaginationRequest>,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") } // Use 'by' delegate
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            debounceJob?.cancel()
            viewModel.hideAllDialogs()
            debounceJob = coroutineScope.launch {
                delay(1500) // Your debounce time
                viewModel.searchTransactionHistory(it, paginationRequest)
            }
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { CustomText("Search transactions...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    query = "" // Clear text
                    // Trigger search for empty string
                    debounceJob?.cancel()
                    viewModel.hideAllDialogs()
                    debounceJob = coroutineScope.launch {
                        delay(1500)
                        viewModel.searchTransactionHistory("", paginationRequest)
                    }
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
        singleLine = true // Good for search bars
    )
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FilterDialog(
    currentRequest: PaginationRequest,
    viewModel: TransactionHistoryViewModel,
    onDismiss: () -> Unit,
    onApplyFilters: (PaginationRequest) -> Unit,
    userViewModel: UserViewModel
) {
    // --- Get staff list from VM ---
    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()
    val staffList = state.value.staffList

    // --- Temporary states, initialized from the current request ---
    var tempPeriod by remember {
        mutableStateOf(currentRequest.filter_by ?: "select::")
    }
    var tempStaffId by remember {
        mutableStateOf(currentRequest.staff_user_id)
    }
    // --- 👇 NEW PAYMENT METHOD STATE 👇 ---
    var tempPaymentMethod by remember {
        mutableStateOf(currentRequest.payment_method)
    }

    // --- Date Range States ---
    var tempStartDate by remember { mutableStateOf(currentRequest.start_date) }
    var tempEndDate by remember { mutableStateOf(currentRequest.end_date) }
    var tempDateRangeText by remember {
        mutableStateOf(
            if (!tempStartDate.isNullOrBlank() && !tempEndDate.isNullOrBlank()) {
                "${tempStartDate} - ${tempEndDate}" // Simple format
            } else {
                "Select Date Range"
            }
        )
    }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // --- Payment Method Dropdown States ---
    var paymentDropdownExpanded by remember { mutableStateOf(false) }
    val paymentOptions = listOf("card", "transfer", "cash")
    val defaultPaymentText = "All Payment Methods"
    var selectedPaymentText by remember(tempPaymentMethod) {
        mutableStateOf(tempPaymentMethod ?: defaultPaymentText)
    }

    // --- Staff Dropdown States ---
    var staffDropdownExpanded by remember { mutableStateOf(false) }
    val defaultStaffText = "All Staff"
    var selectedStaffText by remember(tempStaffId) {
        mutableStateOf(
            staffList.find { it.userId == tempStaffId }?.name ?: defaultStaffText // TODO: Use name
        )
    }
    LaunchedEffect(true){
        if(!userState.value.permissions.contains(SystemPermissions.VIEW_SALES.id)){
          currentRequest.staff_user_id = FirebaseAuth.getInstance().currentUser?.uid
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { CustomText("Filter Transactions") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // --- 2. Filter by Date Range ---
                CustomText("Filter by Date Range", size = titleSmall, modifier = Modifier.clickable(
                    onClick = {
                        showDateRangePicker = true
                        println("akor")
                    }
                ))
                DatePickerField(
                    selectedDateRange = tempDateRangeText,
                    onDateRangeClick = { showDateRangePicker = true },
                    onDateRangeClear = {
                        tempStartDate = null
                        tempEndDate = null
                        tempDateRangeText = "Select Date Range"
                    },
                    modifier = Modifier.fillMaxWidth().clickable(onClick = { showDateRangePicker = true })
                )

                if(userState.value.permissions.contains(SystemPermissions.VIEW_SALES.id)) {
                    // --- 3. Filter by Staff ---
                    CustomText("Filter by Staff", size = titleSmall)
                    StaffDropdown(
                        expanded = staffDropdownExpanded,
                        selectedText = selectedStaffText,
                        staffList = staffList,
                        defaultText = defaultStaffText,
                        onExpandedChange = { staffDropdownExpanded = it },
                        onStaffSelected = { staff ->
                            tempStaffId = staff?.userId // Null if "All Staff"
                        }
                    )
                }

                // --- 4. 👇 NEW PAYMENT METHOD DROPDOWN 👇 ---
                CustomText("Filter by Payment Method", size = titleSmall)
                ExposedDropdownMenuBox(
                    expanded = paymentDropdownExpanded,
                    onExpandedChange = { paymentDropdownExpanded = it },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = selectedPaymentText,
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentDropdownExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = paymentDropdownExpanded,
                        onDismissRequest = { paymentDropdownExpanded = false }
                    ) {
                        // "All" option
                        DropdownMenuItem(
                            text = { CustomText(defaultPaymentText) },
                            onClick = {
                                selectedPaymentText = defaultPaymentText
                                paymentDropdownExpanded = false
                                tempPaymentMethod = null // Clear the filter
                            }
                        )
                        // "card", "transfer", "cash"
                        paymentOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { CustomText(option.capitalize(Locale.ROOT)) },
                                onClick = {
                                    selectedPaymentText = option.capitalize(Locale.ROOT)
                                    paymentDropdownExpanded = false
                                    tempPaymentMethod = option // Set the filter
                                }
                            )
                        }
                    }


                }
                NumberPicker(paginationRequest = currentRequest)
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    // --- APPLY FILTERS ---
                    val finalRequest = currentRequest.copy(
                        filter_by = tempPeriod.takeIf { it != "select::" },
                        staff_user_id = tempStaffId,
                        payment_method = tempPaymentMethod,
                        start_date = tempStartDate,
                        end_date = tempEndDate
                    )

                    // Priority: If a date range is set, it overrides the period.
                    if (finalRequest.start_date != null) {
                        finalRequest.filter_by = null
                    }

                    onApplyFilters(finalRequest)
                    onDismiss()
                }
            ) {
                CustomText("Done")
            }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    // --- CLEAR ALL FILTERS ---
                    val clearRequest = PaginationRequest(
                        limit = currentRequest.limit // Keep the page limit
                    )
                    onApplyFilters(clearRequest)
                    onDismiss()
                }
            ) {
                CustomText("Clear All Filters")
            }
        }
    )

    // --- Date Picker Modal ---
    // This is now fully controlled by the FilterDialog
    if (showDateRangePicker) {
        // We use a different DateRangePickerModal that doesn't call the VM
        LocalDateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onDateRangeSelected = { startDate, endDate, text ->
                tempStartDate = startDate
                tempEndDate = endDate
                tempDateRangeText = text
                // Setting a date range clears the period
                tempPeriod = "select::"
            }
        )
    }
}

// --- Helper Composables for FilterDialog ---






@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDropdown(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("select::", "daily", "weekly", "monthly")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedPeriod,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { CustomText(option.capitalize(Locale.ROOT)) },
                    onClick = {
                        onPeriodSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffDropdown(
    expanded: Boolean,
    selectedText: String,
    staffList: List<Staff>,
    defaultText: String,
    onExpandedChange: (Boolean) -> Unit,
    onStaffSelected: (Staff?) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange,
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.menuAnchor().fillMaxWidth()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { CustomText(defaultText) },
                onClick = {
                    onStaffSelected(null)
                    onExpandedChange(false)
                }
            )
            staffList.forEach { staff ->
                val staffDisplayName = staff.name.ifBlank { staff.userId } // TODO: Use name
                DropdownMenuItem(
                    text = { CustomText(staffDisplayName) },
                    onClick = {
                        onStaffSelected(staff)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

/**
 * A local DateRangePickerModal that doesn't call the ViewModel,
 * but instead just returns the selected values via a callback.
 */
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDateRangePickerModal(
    onDismiss: () -> Unit,
    onDateRangeSelected: (startDate: String?, endDate: String?, text: String) -> Unit
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val startMillis = dateRangePickerState.selectedStartDateMillis
                    val endMillis = dateRangePickerState.selectedEndDateMillis

                    if (startMillis != null && endMillis != null) {
                        val startDate = formatDateFilter(startMillis)
                        val endDate = formatDateFilter(endMillis)
                        val text = "${formatDate(startMillis)} - ${formatDate(endMillis)}"
                        onDateRangeSelected(startDate, endDate, text)
                    }
                    onDismiss()
                }
            ) {
                CustomText("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) { CustomText("Cancel") }
        }
    ) {
        DateRangePicker(
            state = dateRangePickerState,
            title = { CustomText(text = "Select date range") },
            showModeToggle = false,
            modifier = Modifier.height(500.dp)
        )
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NumberPicker(
    paginationRequest: PaginationRequest,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { mutableStateOf(paginationRequest.limit.toString()) } // Default: "Weekly"


    OutlinedTextField(
        placeholder = {CustomText("entries/page")},
        value = selectedOption, onValueChange = {
            try {
                if (it.toInt() > 0) {
                    selectedOption = it
                    paginationRequest.limit = it.toInt()




                }
            } catch (e: Exception) {
                if (it.isEmpty()) {
                    selectedOption = it
                }
            }
        }, trailingIcon = {
            Column() {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "",
                    modifier = Modifier.clickable(onClick = {
                        try {
                            selectedOption = (selectedOption.toInt() + 1).toString()
                            paginationRequest.limit = selectedOption.toInt()
                        } catch (e: Exception) {

                        }
                    })
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "",
                    modifier = Modifier.clickable(onClick = {
                        try {
                            if (selectedOption.toInt() > 0) {
                                selectedOption = (selectedOption.toInt() - 1).toString()



                                    paginationRequest.limit = selectedOption.toInt()


                            }
                        } catch (e: Exception) {

                        }
                    })
                )
            }
        }, modifier = modifier

            .width(120.dp).height(50.dp)
    )

}/**
 * A Composable that calculates and displays the total amount for the
 * transactions on the current page.
 */
@Composable
fun PageTotalCard(transactions: List<OrderModel>) {

    // Only show the card if there are transactions to display
    if (transactions.isNotEmpty()) {

        // 1. Calculate the total
        val pageTotal = transactions.sumOf {
            // Use toDoubleOrNull for safety against bad data
            it.totalAmount.toDoubleOrNull() ?: 0.0
        }

        // 2. Format it (using the Util you already have)
        val formattedTotal = Util.currencyFormat(pageTotal)

        // 3. Display it in a professional-looking card
        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                CustomText(
                    text = "Page Total:",
                    size = titleSmall,
                    fontWeight = FontWeight.SemiBold
                )
                CustomText(
                    text = formattedTotal,
                    size = titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp)) // Add space after the card
    }
}