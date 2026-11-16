package com.sundroid.kobokobo.transactionHistory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Snackbar
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.material3.surfaceColorAtElevation
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.cart.presentation.components.ReceiptDialog
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.ProgressDialog
import com.sundroid.kobokobo.core.presentation.components.StaffDropdown
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.transactionHistory.domain.OrderModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import inventrar.composeapp.generated.resources.eye_icon
import inventrar.composeapp.generated.resources.filter
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// A simple enum to make our layout choice clear
enum class ScreenSize { COMPACT, EXPANDED }

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun TransactionScreen(viewModel: TransactionHistoryViewModel = koinViewModel(), userViewModel: UserViewModel) {
    // State for the new filter dialog
    var showFilterDialog by remember { mutableStateOf(false) }
    var orderToDelete by remember { mutableStateOf<OrderModel?>(null) }
    var orderToMarkPaid by remember { mutableStateOf<OrderModel?>(null) }
    val paginationRequest = remember { 
        val todayDate = Util.formatDate(System.currentTimeMillis())
        mutableStateOf(PaginationRequest(
            start_date = todayDate,
            end_date = todayDate
        )) 
    }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()

    val coroutineScope = rememberCoroutineScope()
    val pdfExporter = com.sundroid.kobokobo.utils.rememberPdfExporter()

    // Fetch data on initial composition
    LaunchedEffect(Unit) {
        if(!userState.value.permissions.contains(SystemPermissions.VIEW_EXPENSES.id)){
            paginationRequest.value.staff_user_id = FirebaseAuth.getInstance().currentUser?.uid
        }
        viewModel.getTransactionHistory(paginationRequest.value)
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        modifier = Modifier.fillMaxSize(),
        snackbarHost = {
            SnackbarHost(
                hostState = state.value.snackBarHostState,
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
                FloatingActionButton(onClick = {
                    val businessName = userViewModel.getUser()?.businessName ?: "Inventra"
                    val staffName = state.value.staffList.find { it.userId == paginationRequest.value.staff_user_id }?.name ?: "All"
                    val dateRange = if (paginationRequest.value.start_date != null) "${paginationRequest.value.start_date} to ${paginationRequest.value.end_date}" else "All Time"
                    val methodText = paginationRequest.value.payment_method ?: "All"
                    val pageStr = paginationRequest.value.page.toString()
                    val rawTransactions = state.value.transactionHistory.orders ?: emptyList()

                    if (rawTransactions.isNotEmpty()) {
                        viewModel.exportTransactionsWithItems(
                            businessName = businessName,
                            staffName = staffName,
                            paymentMethod = methodText,
                            dateRange = dateRange,
                            pageStr = pageStr,
                            rawTransactions = rawTransactions,
                            pdfExporter = pdfExporter
                        )
                    } else {
                        coroutineScope.launch {
                            state.value.snackBarHostState.showSnackbar("No transactions to export")
                        }
                    }
                }, containerColor = MaterialTheme.colorScheme.secondaryContainer) {
                    Icon(imageVector = Icons.Default.Download, contentDescription = "Download PDF", modifier = Modifier.size(24.dp))
                }

                FloatingActionButton(onClick = {showFilterDialog = true}){
                    Icon(painter = painterResource(Res.drawable.filter), contentDescription = "Filter", modifier = Modifier.size(24.dp))
                }
            }
        }
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            val screenSize = if (maxWidth < 800.dp) {
                ScreenSize.COMPACT
            } else {
                ScreenSize.EXPANDED
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (screenSize == ScreenSize.COMPACT) 16.dp else 24.dp, vertical = 16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {


                //Summary Cards
                val transactions = state.value.transactionHistory.orders ?: emptyList()
                TransactionPageTotalCard(
                    transactions = transactions,
                    onViewDetails = { viewModel.onShowPageStats() }
                )

                // --- Transaction List/Table ---
                TransactionList(
                    screenSize = screenSize,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f), // Fills available space
                    canDelete = userState.value.permissions.contains(SystemPermissions.DELETE_SALES.id),
                    onDeleteSale = { orderToDelete = it },
                    canProcessPayment = userState.value.permissions.contains(SystemPermissions.PROCESS_SALES.id),
                    onPaymentStatusClick = { orderToMarkPaid = it }
                )

                // --- Pagination ---
                 PaginationControls(
                    screenSize = screenSize,
                    viewModel = viewModel,
                    paginationRequest = paginationRequest
                )
            }

            // --- Modals and Overlays ---
            if (showFilterDialog) {
                FilterDialog(
                    currentRequest = paginationRequest.value,
                    viewModel = viewModel, 
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { newRequest ->
                        paginationRequest.value = newRequest
                        viewModel.applyFilters(newRequest)
                    },
                    userViewModel = userViewModel
                )
            }
            
            // Page Stats Dialog
            PageStatsDialog(
                showDialog = state.value.showPageStatsDialog,
                isLoading = state.value.isLoadingStats,
                pageStats = state.value.pageStats,
                onDismiss = { viewModel.onHidePageStats() }
            )

            // Progress Dialog
            AnimatedVisibility(visibleState = state.value.showProgressDialog, enter = fadeIn(), exit = androidx.compose.animation.fadeOut()) {
                Box(modifier = Modifier.fillMaxSize().background(Color.Black.copy(alpha = 0.5f)), contentAlignment = Alignment.Center) {
                    ProgressDialog("Loading history...")
                }
            }

            // Receipt Dialog
            ReceiptDialog(
                visibleState = state.value.showReceipt,
                onDismiss = { viewModel.toggleShowReceipt(false) },
                receiptModel = state.value.receiptModel
            )
            
            // Delete Confirm Dialog
            if (orderToDelete != null) {
                AlertDialog(
                    onDismissRequest = { orderToDelete = null },
                    title = { Text("Delete Sale") },
                    text = { Text("Are you sure you want to delete this sale? This action will restore the stock for the items in this order.") },
                    confirmButton = {
                        TextButton(onClick = {
                            val currentUser = userViewModel.getUser()
                            viewModel.deleteSale(
                                reference = orderToDelete!!.orderId,
                                userId = currentUser?.userId ?: "SYSTEM",
                                paginationRequest = paginationRequest.value
                            )
                            orderToDelete = null
                        }) {
                            Text("Delete", color = MaterialTheme.colorScheme.error)
                        }
                    },
                    dismissButton = {
                        TextButton(onClick = { orderToDelete = null }) {
                            Text("Cancel")
                        }
                    }
                )
            }

            orderToMarkPaid?.let { order ->
                SalesPaymentMethodDialog(
                    paymentMethods = state.value.paymentMethods,
                    onDismiss = { orderToMarkPaid = null },
                    onSubmit = { method ->
                        val methodText = state.value.paymentMethods.find { it.id == method }?.name ?: method
                        val processedBy = userState.value.currentUser?.getFullName()?.trim().orEmpty()
                        viewModel.markSalePaid(order.orderId, method, methodText, processedBy, paginationRequest.value)
                        orderToMarkPaid = null
                    }
                )
            }
        }
    }
}



@Composable
fun TransactionList(
    screenSize: ScreenSize,
    viewModel: TransactionHistoryViewModel,
    modifier: Modifier = Modifier,
    canDelete: Boolean = false,
    onDeleteSale: (OrderModel) -> Unit = {},
    canProcessPayment: Boolean = false,
    onPaymentStatusClick: (OrderModel) -> Unit = {}
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val transactions = state.transactionHistory.orders ?: emptyList()

    Box(
        modifier = modifier.fillMaxWidth()
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
             if (screenSize == ScreenSize.COMPACT) {
                 // --- MOBILE LIST ---
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(bottom = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(transactions) { transaction ->
                            TransactionCardItem(
                                transaction = transaction,
                                onViewReceipt = { viewModel.getReceipt(transaction.orderId) },
                                paymentMethods = state.paymentMethods,
                                canDelete = canDelete,
                                onDeleteSale = { onDeleteSale(transaction) },
                                canProcessPayment = canProcessPayment,
                                onPaymentStatusClick = { onPaymentStatusClick(transaction) }
                            )
                    }
                }
            } else {
                // --- DESKTOP TABLE ---
                Column(Modifier.fillMaxSize()) {
                    TransactionTableHeader()
                    Spacer(modifier = Modifier.height(12.dp))
                    LazyColumn(
                        modifier = Modifier.fillMaxSize(),
                        contentPadding = PaddingValues(bottom = 16.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        items(transactions) { transaction ->
                            val index = transactions.indexOf(transaction)
                             // Correct item numbering logic
                            val itemNumber = (state.transactionHistory.limit * (state.transactionHistory.page - 1) + index + 1)
                            
                            TransactionRowItem(
                                itemNumber = itemNumber.toString(),
                                transaction = transaction,
                                onViewReceipt = { viewModel.getReceipt(transaction.orderId) },
                                paymentMethods = state.paymentMethods,
                                canDelete = canDelete,
                                onDeleteSale = { onDeleteSale(transaction) },
                                canProcessPayment = canProcessPayment,
                                onPaymentStatusClick = { onPaymentStatusClick(transaction) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionCardItem(
    transaction: OrderModel,
    onViewReceipt: () -> Unit,
    paymentMethods: List<com.sundroid.kobokobo.business.data.domain.model.PaymentMethod> = emptyList(),
    canDelete: Boolean = false,
    onDeleteSale: () -> Unit = {},
    canProcessPayment: Boolean = false,
    onPaymentStatusClick: () -> Unit = {}
) {
    // Logic to find name:
    val methodText = transaction.paymentMethodText.ifEmpty {
        paymentMethods.find { it.id == transaction.paymentMethod }?.name
            ?: transaction.paymentMethod
    }

    // Date Formatting Logic

    val dateFormat = SimpleDateFormat("MMM dd, yyyy HH:mm", Locale.getDefault())
    val dateString = try { dateFormat.format(Date(transaction.created.toLong())) } catch(e: Exception) { "Invalid Date" }

    ElevatedCard(
        onClick = onViewReceipt,
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 1.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface),
        shape = RoundedCornerShape(16.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
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
                    text = Util.currencyFormat(transaction.totalAmount.toDouble()),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }

            Spacer(modifier = Modifier.size(12.dp))
            HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
            Spacer(modifier = Modifier.size(12.dp))

            // Middle Row: Order ID
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text("Order ID", style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.outline)
                    Text(
                        text = "#${transaction.orderId.uppercase()}", 
                        style = MaterialTheme.typography.bodyLarge, 
                        fontWeight = FontWeight.Medium
                    )
                }
                if (canDelete) {
                    IconButton(onClick = onDeleteSale) {
                        Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                    }
                }
            }

            Spacer(modifier = Modifier.size(12.dp))

            // Bottom Row: Payment Method
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.surfaceContainer, RoundedCornerShape(8.dp))
                    .then(if (!transaction.isPaid && canProcessPayment) Modifier.clickable { onPaymentStatusClick() } else Modifier)
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier.size(4.dp).background(MaterialTheme.colorScheme.secondary, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (transaction.isPaid) "Paid • $methodText" else "Unpaid${if (canProcessPayment) " • Tap to record payment" else ""}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurface 
                    )
                }
            }
        }
    }
}

@Composable
fun TransactionTableHeader() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceColorAtElevation(1.dp)),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("No.", modifier = Modifier.weight(0.5f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Order ID", modifier = Modifier.weight(2f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Date", modifier = Modifier.weight(1.5f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Payment", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("Amount", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.End)
            Text("Actions", modifier = Modifier.weight(1f), fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, textAlign = TextAlign.Center)
        }
    }
}

@Composable
fun TransactionRowItem(
    itemNumber: String,
    transaction: OrderModel,
    onViewReceipt: () -> Unit,
    paymentMethods: List<com.sundroid.kobokobo.business.data.domain.model.PaymentMethod> = emptyList(),
    canDelete: Boolean = false,
    onDeleteSale: () -> Unit = {},
    canProcessPayment: Boolean = false,
    onPaymentStatusClick: () -> Unit = {}
) {
    val methodText = if (transaction.paymentMethodText.isNotEmpty()) {
         transaction.paymentMethodText
    } else {
         paymentMethods.find { it.id == transaction.paymentMethod }?.name ?: transaction.paymentMethod 
    }

    Card(
        modifier = Modifier.fillMaxWidth().clickable { onViewReceipt() },
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(itemNumber, modifier = Modifier.weight(0.5f), style = MaterialTheme.typography.bodyMedium)
            
            Column(Modifier.weight(2f)) {
                Text(transaction.orderId, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.Medium)
            }
            
            Text(transaction.created, modifier = Modifier.weight(1.5f), style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            
            Text(
                if (transaction.isPaid) "Paid • $methodText" else "Unpaid",
                modifier = Modifier.weight(1f).then(if (!transaction.isPaid && canProcessPayment) Modifier.clickable { onPaymentStatusClick() } else Modifier),
                style = MaterialTheme.typography.bodyMedium,
                color = if (transaction.isPaid) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.error
            )

            Text(
                Util.currencyFormat(transaction.totalAmount.toDouble()), 
                modifier = Modifier.weight(1f), 
                style = MaterialTheme.typography.bodyMedium, 
                fontWeight = FontWeight.Bold, 
                color = MaterialTheme.colorScheme.primary,
                textAlign = TextAlign.End
            )
            
            Box(Modifier.weight(1f), contentAlignment = Alignment.Center) {
                 Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                     FilledIconButton(
                         onClick = onViewReceipt,
                         modifier = Modifier.size(32.dp),
                         colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                     ) {
                         Icon(painter = painterResource(Res.drawable.eye_icon), contentDescription = "View", tint = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(16.dp))
                     }
                     if (canDelete) {
                         FilledIconButton(
                             onClick = onDeleteSale,
                             modifier = Modifier.size(32.dp),
                             colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
                         ) {
                             Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.onErrorContainer, modifier = Modifier.size(16.dp))
                         }
                     }
                 }
            }
        }
    }
}

@Composable
private fun SalesPaymentMethodDialog(
    paymentMethods: List<com.sundroid.kobokobo.business.data.domain.model.PaymentMethod>,
    onDismiss: () -> Unit,
    onSubmit: (String) -> Unit
) {
    var selectedMethod by remember { mutableStateOf("") }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record payment") },
        text = {
            Column {
                if (paymentMethods.isEmpty()) Text("No payment methods are configured.")
                paymentMethods.forEach { method ->
                    Row(
                        modifier = Modifier.fillMaxWidth().clickable { selectedMethod = method.id.ifEmpty { method.name } },
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(selected = selectedMethod == method.id.ifEmpty { method.name }, onClick = { selectedMethod = method.id.ifEmpty { method.name } })
                        Text(method.name)
                    }
                }
            }
        },
        confirmButton = { Button(onClick = { onSubmit(selectedMethod) }, enabled = selectedMethod.isNotBlank()) { Text("Submit") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
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
    val state by viewModel.state.collectAsStateWithLifecycle()
    val page = state.page
    val totalPages = state.totalPages

    val onPageChange: (Long) -> Unit = { newPage ->
        debounceJob?.cancel()
        viewModel.hideAllDialogs()
        debounceJob = coroutineScope.launch {
            paginationRequest.value.page = newPage
            viewModel.getTransactionHistory(paginationRequest.value)
        }
    }

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        IconButton(
            onClick = { onPageChange(page - 1L) },
            enabled = page > 1,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
        }

        Spacer(modifier = Modifier.width(16.dp))

        Text(
            text = "Page $page of $totalPages",
            style = MaterialTheme.typography.labelLarge,
            color = MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(16.dp))

        IconButton(
            onClick = { onPageChange(page + 1L) },
            enabled = page < totalPages,
            colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
        }
    }
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
    
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.state.collectAsStateWithLifecycle()
    val staffList = state.staffList

    var tempStartDate by remember { mutableStateOf(currentRequest.start_date) }
    var tempEndDate by remember { mutableStateOf(currentRequest.end_date) }
    var tempPaymentMethod by remember { mutableStateOf(currentRequest.payment_method) }
    var tempStaffId by remember { mutableStateOf(currentRequest.staff_user_id) }
    
    var showDateRangePicker by remember { mutableStateOf(false) }
    var includeTime by remember { mutableStateOf(false) }
    var showStartTimePicker by remember { mutableStateOf(false) }
    var showEndTimePicker by remember { mutableStateOf(false) }
    var tempStartMillis by remember { mutableStateOf<Long?>(null) }
    var tempEndMillis by remember { mutableStateOf<Long?>(null) }

    LaunchedEffect(true){
        if(!userState.permissions.contains(SystemPermissions.VIEW_SALES.id)){
            tempStaffId = FirebaseAuth.getInstance().currentUser?.uid
        }
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Transactions", fontWeight = FontWeight.Bold) },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                 // Date Filter
                 Text("Date Range", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                 OutlinedTextField(
                     value = if(tempStartDate != null) "$tempStartDate - ${tempEndDate?:""}" else "",
                     onValueChange = {},
                     readOnly = true,
                     label = { Text("Select Date") },
                     trailingIcon = { Icon(painterResource(Res.drawable.calendar), null) },
                     modifier = Modifier.fillMaxWidth().clickable { showDateRangePicker = true },
                     enabled = false, // Handle click on parent or overlay
                     colors = OutlinedTextFieldDefaults.colors(
                         disabledTextColor = MaterialTheme.colorScheme.onSurface,
                         disabledBorderColor = MaterialTheme.colorScheme.outline,
                         disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
                         disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
                     )
                 )
                 // A transparent box to catch clicks over the disabled text field if needed
                 Box(Modifier.height(0.dp).clickable { showDateRangePicker = true })

                 Row(verticalAlignment = Alignment.CenterVertically) {
                     androidx.compose.material3.Checkbox(
                         checked = includeTime,
                         onCheckedChange = { includeTime = it }
                     )
                     Text("Include Specific Time")
                 }

                 // Payment Method Filter
                 Text("Payment Method", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                 Row(
                     modifier = Modifier.fillMaxWidth().horizontalScroll(rememberScrollState()),
                     horizontalArrangement = Arrangement.spacedBy(8.dp)
                 ) {
                     // Include "All" option
                     val allMethod = com.sundroid.kobokobo.business.data.domain.model.PaymentMethod(id = "All", name = "All")
                     val methods = listOf(allMethod) + state.paymentMethods
                     
                     methods.forEach { method ->
                         val isSelected = if(method.id == "All") tempPaymentMethod == null else tempPaymentMethod == method.id
                         androidx.compose.material3.FilterChip(
                             selected = isSelected,
                             onClick = { 
                                 tempPaymentMethod = if(method.id == "All") null else method.id
                             },
                             label = { Text(method.name) }
                         )
                     }
                 }

                 // Staff Filter
                 if(userState.permissions.contains(SystemPermissions.VIEW_SALES.id)) {
                     Text("Filter by Staff", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                     var expanded by remember { mutableStateOf(false) }
                     val selectedStaffName = staffList.find { it.userId == tempStaffId }?.name ?: "All Staff"
                     
                     StaffDropdown(
                         expanded = expanded,
                         selectedText = selectedStaffName,
                         staffList = staffList,
                         onExpandedChange = { expanded = it },
                         onStaffSelected = { staff ->
                             tempStaffId = staff?.userId
                         }
                     )
                 }

                 // Show Entries Filter
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
            androidx.compose.material3.Button(
                onClick = {
                    val newRequest = currentRequest.copy(
                        start_date = tempStartDate,
                        end_date = tempEndDate,
                        payment_method = tempPaymentMethod,
                        staff_user_id = tempStaffId,
                        page = 1 // Reset to page 1 on filter
                    )
                    onApplyFilters(newRequest)
                    onDismiss()
                }
            ) {
                Text("Apply Filters")
            }
        },
        dismissButton = {
            androidx.compose.material3.TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
    
     if (showDateRangePicker) {
        val datePickerState = rememberDateRangePickerState()
        val confirmEnabled = datePickerState.selectedStartDateMillis != null && datePickerState.selectedEndDateMillis != null
        
        androidx.compose.material3.DatePickerDialog(
            onDismissRequest = { showDateRangePicker = false },
            confirmButton = {
                TextButton(
                    onClick = {
                        tempStartMillis = datePickerState.selectedStartDateMillis
                        tempEndMillis = datePickerState.selectedEndDateMillis
                        
                        showDateRangePicker = false
                        
                        if (includeTime) {
                            showStartTimePicker = true
                        } else {
                            tempStartDate = tempStartMillis?.let { Util.formatDate(it) }
                            tempEndDate = tempEndMillis?.let { Util.formatDate(it) }
                        }
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

    if (showStartTimePicker) {
        val timePickerState = androidx.compose.material3.rememberTimePickerState()
        TimePickerDialog(
            title = "Select Start Time",
            onDismissRequest = { showStartTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showStartTimePicker = false
                    tempStartDate = tempStartMillis?.let { formatDateTime(it, timePickerState.hour, timePickerState.minute) }
                    showEndTimePicker = true
                }) { Text("Next") }
            },
            dismissButton = {
                TextButton(onClick = { showStartTimePicker = false }) { Text("Cancel") }
            }
        ) {
            androidx.compose.material3.TimePicker(state = timePickerState)
        }
    }

    if (showEndTimePicker) {
        val timePickerState = androidx.compose.material3.rememberTimePickerState()
        TimePickerDialog(
            title = "Select End Time",
            onDismissRequest = { showEndTimePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    showEndTimePicker = false
                    tempEndDate = tempEndMillis?.let { formatDateTime(it, timePickerState.hour, timePickerState.minute) }
                }) { Text("OK") }
            },
            dismissButton = {
                TextButton(onClick = { showEndTimePicker = false }) { Text("Cancel") }
            }
        ) {
            androidx.compose.material3.TimePicker(state = timePickerState)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TimePickerDialog(
    title: String = "Select Time",
    onDismissRequest: () -> Unit,
    confirmButton: @Composable () -> Unit,
    dismissButton: @Composable () -> Unit,
    content: @Composable () -> Unit,
) {
    AlertDialog(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = MaterialTheme.colorScheme.surface,
                shape = RoundedCornerShape(size = 12.dp)
            ),
        onDismissRequest = onDismissRequest
    ) {
        Column(
            modifier = Modifier
                .background(color = MaterialTheme.colorScheme.surface)
                .padding(top = 28.dp, start = 20.dp, end = 20.dp, bottom = 12.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(title, modifier = Modifier.padding(bottom = 16.dp), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            content()
            Row(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                dismissButton()
                Spacer(modifier = Modifier.width(8.dp))
                confirmButton()
            }
        }
    }
}

@Composable
fun TransactionPageTotalCard(
    transactions: List<OrderModel>,
    onViewDetails: () -> Unit
) {
    if (transactions.isNotEmpty()) {
        val pageTotal = transactions.sumOf { (it.totalAmount.replace(",","").toDoubleOrNull() ?: 0.0) }
        val formattedTotal = Util.currencyFormat(pageTotal)

        Card(
            modifier = Modifier.fillMaxWidth(),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth().padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                 Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.weight(1f).clickable(
                     onClick = onViewDetails
                 )) {
                     Text("Total Sales (Current Page)", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onPrimaryContainer)
                     Spacer(modifier = Modifier.width(8.dp))
                 }
                 
                 Text(
                    text = formattedTotal,
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimaryContainer
                )
            }
        }
    }
}

@Composable
fun PageStatsDialog(
    showDialog: Boolean,
    isLoading: Boolean,
    pageStats: Any?, // Avoiding cyclic dependency if state class not visible, but it should be. Using dynamic cast or pass separate fields if needed. Ideally access State directly.
    onDismiss: () -> Unit
) {
    if (showDialog) {
        Dialog(onDismissRequest = onDismiss) {
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "Transaction Details",
                        style = MaterialTheme.typography.headlineSmall,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    if (isLoading) {
                        CircularProgressIndicator()
                        Spacer(modifier = Modifier.height(16.dp))
                        Text("Calculating stats from receipts...")
                    } else {
                        // Cast stats safely or pass structural data
                        // Assuming valid data structure passed from parent
                         (pageStats as? TransactionPageStats)?.let { stats ->
                            // Summary Box
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceEvenly
                            ) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Sales", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        Util.currencyFormat(stats.totalSales),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.primary
                                    )
                                }
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("Total Profit", style = MaterialTheme.typography.labelMedium)
                                    Text(
                                        Util.currencyFormat(stats.totalProfit),
                                        style = MaterialTheme.typography.titleMedium,
                                        fontWeight = FontWeight.Bold,
                                        color = MaterialTheme.colorScheme.tertiary
                                    )
                                }
                            }
                            
                            Spacer(modifier = Modifier.height(24.dp))
                            HorizontalDivider()
                            Spacer(modifier = Modifier.height(12.dp))
                            
                            Text("Items Sold", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold, modifier = Modifier.align(Alignment.Start))
                            Spacer(modifier = Modifier.height(8.dp))
                            
                            Box(modifier = Modifier.height(200.dp)) { // Max height for list
                                LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                                    items(stats.itemBreakdown) { item ->
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween
                                        ) {
                                            Text(
                                                item.name,
                                                style = MaterialTheme.typography.bodyMedium,
                                                maxLines = 1,
                                                overflow = TextOverflow.Ellipsis,
                                                modifier = Modifier.weight(1f)
                                            )
                                            Text(
                                                "${item.quantity}",
                                                style = MaterialTheme.typography.bodyMedium,
                                                fontWeight = FontWeight.Bold
                                            )
                                        }
                                        Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                                    }
                                }
                            }
                        }
                    }
                    
                    Spacer(modifier = Modifier.height(24.dp))
                    TextButton(onClick = onDismiss, modifier = Modifier.fillMaxWidth()) {
                        Text("Close")
                    }
                }
            }
        }
    }
}

@Composable
fun TransactionHistoryNumberPicker(
    paginationRequest: PaginationRequest,
    modifier: Modifier = Modifier
) {
    var selectedOption by remember { mutableStateOf(paginationRequest.limit.toString()) }

    OutlinedTextField(
        placeholder = { Text("entries/page") },
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
}

@Composable
fun TableCell(text: String, modifier: Modifier) {
    CustomText(text, style = 14.sp, modifier = modifier)
}


fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatDateFilter(timestamp: Long): String {
    val sdf = java.text.SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(java.util.Date(timestamp))
}

fun formatDateTime(millis: Long, hour: Int, minute: Int): String {
    val cal = java.util.Calendar.getInstance().apply {
        timeInMillis = millis
        set(java.util.Calendar.HOUR_OF_DAY, hour)
        set(java.util.Calendar.MINUTE, minute)
    }
    val formatter = java.text.SimpleDateFormat("yyyy-MM-dd HH:mm")
    return formatter.format(cal.time)
}
