package com.sundroid.kobokobo.laundry.presentation.history

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.ProgressDialog
import com.sundroid.kobokobo.core.presentation.components.StaffDropdown
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryTransaction
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionHistoryNumberPicker
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import inventrar.composeapp.generated.resources.eye_icon
import inventrar.composeapp.generated.resources.filter
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryTransactionHistoryScreen(
    viewModel: LaundryTransactionHistoryViewModel,
    userViewModel: UserViewModel,
    onNavigateBack: () -> Unit
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    val paginationRequest = remember {
        val todayDate = Util.formatDate(System.currentTimeMillis())
        mutableStateOf(
            PaginationRequest(
                start_date = todayDate,
                end_date = todayDate
            )
        )
    }

    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val pdfExporter = com.sundroid.kobokobo.utils.rememberPdfExporter()

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = state.snackBarHostState,
                modifier = Modifier.padding(16.dp),
                snackbar = { snackbarData ->
                    Snackbar(
                        action = {
                            snackbarData.visuals.actionLabel?.let { actionLabel ->
                                TextButton(
                                    onClick = { snackbarData.performAction() }
                                ) {
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
            Column(horizontalAlignment = Alignment.End, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                FloatingActionButton(
                    onClick = {
                        val businessName = userState.currentUser?.businessName ?: "Inventra"
                        val staffName = state.staffList.find { it.userId == paginationRequest.value.staff_user_id }?.name ?: "All"
                        val dateRange = if (paginationRequest.value.start_date != null) "${paginationRequest.value.start_date} to ${paginationRequest.value.end_date}" else "All Time"
                        val methodText = paginationRequest.value.payment_method ?: "All"
                        val pageStr = paginationRequest.value.page.toString()
                        val rawTransactions = state.filteredTransactions

                        if (rawTransactions.isNotEmpty()) {
                            viewModel.exportTransactions(
                                businessName = businessName,
                                staffName = staffName,
                                paymentMethod = methodText,
                                dateRange = dateRange,
                                pageStr = pageStr,
                                transactions = rawTransactions,
                                pdfExporter = pdfExporter
                            )
                        } else {
                            coroutineScope.launch {
                                state.snackBarHostState.showSnackbar("No transactions to export")
                            }
                        }
                    },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer
                ) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download PDF", modifier = Modifier.size(24.dp))
                }

                FloatingActionButton(onClick = { showFilterDialog = true }) {
                    Icon(painter = painterResource(Res.drawable.filter), contentDescription = "Filter", modifier = Modifier.size(24.dp))
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {

                // Summary Card
                val transactions = state.filteredTransactions
                if (transactions.isNotEmpty()) {
                    val pageTotal = transactions.sumOf { it.totalAmount }
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Total Sales (Filtered)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                            Text(
                                text = Util.currencyFormat(pageTotal),
                                style = MaterialTheme.typography.headlineSmall,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        }
                    }
                }

                // Transaction List
                Card(
                    modifier = Modifier.weight(1f).fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp),
                    border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
                ) {
                    if (transactions.isEmpty()) {
                        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Icon(
                                    Icons.Default.Search,
                                    contentDescription = null,
                                    modifier = Modifier.size(48.dp),
                                    tint = MaterialTheme.colorScheme.outline
                                )
                                Spacer(Modifier.height(8.dp))
                                Text("No transactions found", color = MaterialTheme.colorScheme.outline)
                            }
                        }
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(16.dp),
                            verticalArrangement = Arrangement.spacedBy(12.dp)
                        ) {
                            items(transactions) { transaction ->
                                LaundryTransactionCardItem(
                                    transaction = transaction,
                                    onViewReceipt = { viewModel.viewReceipt(transaction) },
                                    paymentMethods = state.paymentMethods
                                )
                            }
                        }
                    }
                }

                // Pagination Controls
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = {
                            val newPage = state.page - 1
                            paginationRequest.value = paginationRequest.value.copy(page = newPage)
                            viewModel.applyFilters(paginationRequest.value)
                        },
                        enabled = state.page > 1,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Text(
                        text = "Page ${state.page} of ${state.totalPages}",
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(modifier = Modifier.width(16.dp))

                    IconButton(
                        onClick = {
                            val newPage = state.page + 1
                            paginationRequest.value = paginationRequest.value.copy(page = newPage)
                            viewModel.applyFilters(paginationRequest.value)
                        },
                        enabled = state.page < state.totalPages,
                        colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
                    }
                }
            }

            if (showFilterDialog) {
                LaundryFilterDialog(
                    currentRequest = paginationRequest.value,
                    state = state,
                    userPermissions = userState.permissions,
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { newRequest ->
                        paginationRequest.value = newRequest
                        viewModel.applyFilters(newRequest)
                    }
                )
            }

            AnimatedVisibility(visibleState = androidx.compose.animation.core.MutableTransitionState(state.isLoading), enter = fadeIn(), exit = fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    ProgressDialog("Loading history...")
                }
            }

            if (state.showReceipt && state.selectedTransaction != null) {
                LaundryReceiptDialog(
                    transaction = state.selectedTransaction!!,
                    onDismiss = { viewModel.hideReceipt() }
                )
            }
        }
    }
}

@Composable
fun LaundryTransactionCardItem(
    transaction: LaundryTransaction,
    onViewReceipt: () -> Unit,
    paymentMethods: List<com.sundroid.kobokobo.business.data.domain.model.PaymentMethod> = emptyList()
) {
    val methodText = paymentMethods.find { it.id == transaction.paymentMethod }?.name ?: transaction.paymentMethod

    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateString = try { dateFormat.format(Date(transaction.timestamp)) } catch(e: Exception) { "Invalid Date" }

    ElevatedCard(
        onClick = onViewReceipt,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CalendarToday,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.outline
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = dateString,
                        style = MaterialTheme.typography.labelLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                Text(
                    text = Util.currencyFormat(transaction.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.size(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.size(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Ref", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "#${transaction.reference.uppercase()}",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Medium
                    )
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.secondary, CircleShape))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = methodText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryFilterDialog(
    currentRequest: PaginationRequest,
    state: LaundryHistoryState,
    userPermissions: List<String>,
    onDismiss: () -> Unit,
    onApplyFilters: (PaginationRequest) -> Unit
) {
    var tempStartDate by remember { mutableStateOf(currentRequest.start_date) }
    var tempEndDate by remember { mutableStateOf(currentRequest.end_date) }
    var tempPaymentMethod by remember { mutableStateOf(currentRequest.payment_method) }
    var tempStaffId by remember { mutableStateOf(currentRequest.staff_user_id) }
    var showDateRangePicker by remember { mutableStateOf(false) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Transactions", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                Text("Date Range", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                OutlinedTextField(
                    value = if(tempStartDate != null) "$tempStartDate - ${tempEndDate?:""}" else "",
                    onValueChange = {},
                    readOnly = true,
                    label = { Text("Select Date") },
                    trailingIcon = { Icon(painterResource(Res.drawable.calendar), null) },
                    modifier = Modifier.fillMaxWidth().clickable { showDateRangePicker = true },
                    enabled = false,
                    colors = OutlinedTextFieldDefaults.colors(
                        disabledTextColor = MaterialTheme.colorScheme.onSurface,
                        disabledBorderColor = MaterialTheme.colorScheme.outline,
                        disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                        disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                )
                Box(Modifier.height(0.dp).clickable { showDateRangePicker = true })

                Text("Payment Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                Row(
                    modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    val allMethod = com.sundroid.kobokobo.business.data.domain.model.PaymentMethod(id = "All", name = "All")
                    val methods = listOf(allMethod) + state.paymentMethods

                    methods.forEach { method ->
                        val isSelected = if(method.id == "All") tempPaymentMethod == null || tempPaymentMethod == "All" else tempPaymentMethod == method.id
                        FilterChip(
                            selected = isSelected,
                            onClick = {
                                tempPaymentMethod = if(method.id == "All") null else method.id
                            },
                            label = { Text(method.name) }
                        )
                    }
                }

                if(userPermissions.contains(SystemPermissions.VIEW_SALES.id)) {
                    Text("Filter by Staff", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    var expanded by remember { mutableStateOf(false) }
                    val selectedStaffName = state.staffList.find { it.userId == tempStaffId }?.name ?: "All Staff"

                    StaffDropdown(
                        expanded = expanded,
                        selectedText = selectedStaffName,
                        staffList = state.staffList,
                        onExpandedChange = { expanded = it },
                        onStaffSelected = { staff ->
                            tempStaffId = staff?.userId
                        }
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Show:")
                    TransactionHistoryNumberPicker(currentRequest)
                    Text("Entries")
                }
            }
        },
        confirmButton = {
            Button(
                onClick = {
                    val newRequest = currentRequest.copy(
                        start_date = tempStartDate,
                        end_date = tempEndDate,
                        payment_method = tempPaymentMethod,
                        staff_user_id = tempStaffId,
                        page = 1
                    )
                    onApplyFilters(newRequest)
                    onDismiss()
                }
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )

    if (showDateRangePicker) {
        val datePickerState = rememberDateRangePickerState()
        val confirmEnabled = datePickerState.selectedStartDateMillis != null && datePickerState.selectedEndDateMillis != null

        DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        val start = datePickerState.selectedStartDateMillis?.let { Util.formatDate (it) }
                        val end = datePickerState.selectedEndDateMillis?.let { Util.formatDate(it) }
                        tempStartDate = start
                        tempEndDate = end
                        showDateRangePicker = false
                    },
                    enabled = confirmEnabled
                ) {
                    Text("OK")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDateRangePicker = false }) {
                    Text("Cancel")
                }
            }
        ) {
            DateRangePicker(state = datePickerState)
        }
    }
}

@Composable
fun LaundryReceiptDialog(
    transaction: LaundryTransaction,
    onDismiss: () -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Receipt: ${transaction.reference}") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                Text("Staff: ${transaction.staffName}", style = MaterialTheme.typography.bodyMedium)
                Spacer(modifier = Modifier.height(8.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))

                transaction.items.forEach { item ->
                    Row(
                        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text("${item.materialName} - ${item.serviceType}", fontWeight = FontWeight.Medium)
                            Text("${item.quantity} x ${Util.currencyFormat(item.unitPrice)}", style = MaterialTheme.typography.bodySmall)
                        }
                        Text(Util.currencyFormat(item.getSubTotal()), fontWeight = FontWeight.Bold)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                HorizontalDivider()
                Spacer(modifier = Modifier.height(8.dp))
                
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text("Total", fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(Util.currencyFormat(transaction.totalAmount), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
