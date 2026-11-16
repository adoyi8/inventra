package com.sundroid.kobokobo.expense.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.presentation.components.DatePickerField
import com.sundroid.kobokobo.core.presentation.components.LocalDateRangePickerModal
import com.sundroid.kobokobo.core.presentation.components.NumberPicker
import com.sundroid.kobokobo.core.presentation.components.PaginationButton
import com.sundroid.kobokobo.core.presentation.components.PeriodDropdown
import com.sundroid.kobokobo.core.presentation.components.ProgressDialog
import com.sundroid.kobokobo.core.presentation.components.StaffDropdown
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.expense.domain.Expense
import com.sundroid.kobokobo.expense.domain.ExpenseDto
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.transactionHistory.presentation.ScreenSize
import com.sundroid.kobokobo.transactionHistory.presentation.TableCell
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.filter
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.util.Locale

// Note: Ensure you have an 'OrderModel' type defined, or replace 'OrderModel'
// with your actual 'Expense' data class in the 'ExpenseCardItem'
// and 'ExpenseRowItem' function signatures.
// Assuming your Expense model looks like this from Step 1:
// data class Expense(
//     val expenseId: String = "",
//     val description: String = "",
//     val amount: Double = 0.0,
//     val created: Long = 0L,
//     val createdBy: String = "",
//     val paymentMethod: String = "",
//     val category: String = ""
// )


// --- Main Entry Composable ---

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel = koinViewModel(),
    staffId: String, // Passed from the parent (e.g., Dashboard), required for creating expenses
    userViewModel: UserViewModel
) {
    var showFilterDialog = remember { mutableStateOf(false) }
    var showAddExpenseDialog by remember { mutableStateOf(false) }
    val paginationRequest = remember { mutableStateOf(PaginationRequest()) }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()

    // Fetch initial data
    LaunchedEffect(Unit) {
            if(!userState.value.permissions.contains(SystemPermissions.VIEW_EXPENSES.id)){
                paginationRequest.value.staff_user_id = FirebaseAuth.getInstance().currentUser?.uid
            }
        viewModel.getExpenses(paginationRequest.value)
    }

    Scaffold(
        floatingActionButton = {
            if(userState.value.permissions.contains(SystemPermissions.ADD_EXPENSES.id)){


                    FloatingActionButton(
                        onClick = { showAddExpenseDialog = true },
                        modifier = Modifier.padding(bottom = 50.dp)
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "Add Expense")
                    }

        }}
    ) { padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(padding)
        ) {
            val screenSize = if (maxWidth < 600.dp) ScreenSize.COMPACT else ScreenSize.EXPANDED

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (screenSize == ScreenSize.COMPACT) 16.dp else 24.dp)
            ) {
                // --- Page Total Summary ---
                val pageExpenses = state.value.expenseHistory.expenses
                ExpensePageTotalCard(expenses = pageExpenses, showFilterDialog)

                // --- Expense List / Table ---
                ExpenseList(
                    screenSize = screenSize,
                    viewModel = viewModel,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Pagination ---
                ExpensePaginationControls(
                    screenSize = screenSize,
                    viewModel = viewModel,
                    paginationRequest = paginationRequest
                )
            }

            // --- Dialogs and Overlays ---

            // Empty State
            if (state.value.expenseHistory.expenses.isNullOrEmpty() && !state.value.showProgressDialog.targetState) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("There are no expenses to display")
                }
            }

            // Filter Dialog
            if (showFilterDialog.value) {
                ExpenseFilterDialog(
                    currentRequest = paginationRequest.value,
                    viewModel = viewModel, // For staff list and categories
                    onDismiss = { showFilterDialog.value = false },
                    onApplyFilters = { newRequest ->
                        paginationRequest.value = newRequest
                        viewModel.applyFilters(newRequest)
                    },
                    userViewModel = userViewModel
                )
            }

            // Add Expense Dialog
            if (showAddExpenseDialog) {
                AddExpenseDialog(
                    staffId = staffId, // Pass the logged-in staff's ID
                    viewModel = viewModel,
                    onDismiss = { showAddExpenseDialog = false },
                    onExpenseAdded = { dto ->
                        viewModel.addExpense(dto, paginationRequest.value)
                    },
                    userViewModel = userViewModel
                )
            }

            // Progress Dialog
            AnimatedVisibility(visible = state.value.showProgressDialog.targetState) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    ProgressDialog("Processing...") // Assuming ProgressDialog is a composable you have
                }
            }

            // Snackbar
            SnackbarHost(
                hostState = state.value.snackBarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

// --- Search and Entry Controls ---

@Composable
fun ExpenseSearchAndEntryControls(
    screenSize: ScreenSize,
    paginationRequest: MutableState<PaginationRequest>,
    viewModel: ExpenseViewModel,
    onFilterClick: () -> Unit
) {
    if (screenSize == ScreenSize.COMPACT) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseSearchBar(
                    viewModel = viewModel,
                    paginationRequest = paginationRequest,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFilterClick) {
                    Icon( painter = painterResource(Res.drawable.filter), contentDescription = "Filter expenses")
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
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text("Show:")
                NumberPicker(
                    value = paginationRequest.value.limit,
                    onValueChange = { paginationRequest.value.limit = it },
                    modifier = Modifier.width(100.dp)
                )
                Text("Entries")
            }

            Row(
                modifier = Modifier.width(400.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ExpenseSearchBar(
                    viewModel = viewModel,
                    paginationRequest = paginationRequest,
                    modifier = Modifier.weight(1f)
                )
                IconButton(onClick = onFilterClick) {
                    Icon(painter =  painterResource(Res.drawable.filter), contentDescription = "Filter expenses")
                }
            }
        }
    }
}

@Composable
fun ExpenseSearchBar(
    viewModel: ExpenseViewModel,
    paginationRequest: MutableState<PaginationRequest>,
    modifier: Modifier = Modifier
) {
    var query by remember { mutableStateOf("") }
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()

    OutlinedTextField(
        value = query,
        onValueChange = {
            query = it
            debounceJob?.cancel()
            debounceJob = coroutineScope.launch {
                delay(1500)
                viewModel.searchExpenses(it, paginationRequest)
            }
        },
        modifier = modifier.fillMaxWidth(),
        placeholder = { Text("Search by description...") },
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = {
                    query = ""
                    debounceJob?.cancel()
                    debounceJob = coroutineScope.launch {
                        delay(1500)
                        viewModel.searchExpenses("", paginationRequest)
                    }
                }) {
                    Icon(Icons.Default.Clear, contentDescription = "Clear search")
                }
            }
        },
        singleLine = true
    )
}

// --- Page Total Card ---

@Composable
fun ExpensePageTotalCard(expenses: List<Expense>, showFilterDialog: MutableState<Boolean>) {
    if (expenses.isNotEmpty()) {
        val pageTotal = expenses.sumOf { it.amount }

        // Assuming you have a Util.currencyFormat function
        val formattedTotal = Util.currencyFormat(pageTotal)

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier
                .fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .weight(2.5f),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Page Total:",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                    Text(
                        text = formattedTotal,
                        style = MaterialTheme.typography.titleLarge,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Icon(Icons.Default.FilterList, contentDescription = "filter", modifier = Modifier.weight(1f)
                    .clickable(
                        onClick = {
                            showFilterDialog.value = true
                        }
                    ))
            }

        }

        Spacer(modifier = Modifier.height(16.dp))
    }
}

// --- Responsive List ---

@Composable
fun ExpenseList(
    screenSize: ScreenSize,
    viewModel: ExpenseViewModel,
    modifier: Modifier = Modifier
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val expenses = state.value.expenseHistory.expenses

    Box(modifier = modifier.fillMaxWidth()) {
        if (screenSize == ScreenSize.COMPACT) {
            // --- COMPACT (Mobile) ---
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(expenses) { expense ->
                    ExpenseCardItem(expense = expense)
                }
            }
        } else {
            // --- EXPANDED (Desktop) ---
            val horizontalScrollState = rememberScrollState()

            Box(modifier = Modifier.fillMaxSize()) {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .horizontalScroll(horizontalScrollState)
                ) {
                    item {
                        ExpenseTableHeader()
                    }
                    items(expenses) { expense ->
                        val index = expenses.indexOf(expense)
                        val itemNumber = (state.value.expenseHistory.limit * (state.value.expenseHistory.page - 1) + index + 1)
                        ExpenseRowItem(
                            itemNumber = itemNumber.toString(),
                            expense = expense,
                            staffList = state.value.staffList // Pass staffList for name lookup
                        )
                    }
                }

                // HorizontalScrollbar(
                //     modifier = Modifier
                //         .align(Alignment.BottomStart)
                //         .fillMaxWidth(),
                //     adapter = rememberScrollbarAdapter(horizontalScrollState)
                // )
            }
        }
    }
}

// --- List Items ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseCardItem(expense: Expense) {
    Card(
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
                Text(
                    text = expense.description,
                    style = MaterialTheme.typography.titleMedium,
                    modifier = Modifier.weight(1f)
                )
                Text(
                    text = Util.currencyFormat(expense.amount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.padding(start = 8.dp)
                )
            }
            Text(
                text = "Category: ${expense.category}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Payment: ${expense.paymentMethod.capitalize(Locale.ROOT)}",
                style = MaterialTheme.typography.bodyMedium
            )
            Text(
                text = "Date: ${Util.formatDate(expense.created)}", // Assuming formatDate exists
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun ExpenseTableHeader() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Adjust widths as needed
        TableCell("No.", modifier = Modifier.width(80.dp))
        TableCell("Description", modifier = Modifier.width(300.dp))
        TableCell("Category", modifier = Modifier.width(150.dp))
        TableCell("Date & Amount", modifier = Modifier.width(200.dp))
        TableCell("Payment", modifier = Modifier.width(150.dp))
        TableCell("Recorded By", modifier = Modifier.width(150.dp))
    }
}

@Composable
fun ExpenseRowItem(
    itemNumber: String,
    expense: Expense,
    staffList: List<Staff> // To find staff name
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
        TableCell(itemNumber, modifier = Modifier.width(80.dp))
        TableCell(expense.description, modifier = Modifier.width(300.dp))
        TableCell(expense.category, modifier = Modifier.width(150.dp))

        Column(modifier = Modifier.width(200.dp)) {
            Text(Util.formatDate(expense.created), style = MaterialTheme.typography.bodyMedium)
            Text(
                Util.currencyFormat(expense.amount),
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.bodyMedium
            )
        }

        TableCell(expense.paymentMethod.capitalize(Locale.ROOT), modifier = Modifier.width(150.dp))

        // Find staff name from ID
        val staffName = staffList.find { it.userId == expense.createdBy }?.roleId // TODO: Use .name
            ?: expense.createdBy.take(8) // Fallback
        TableCell(staffName, modifier = Modifier.width(150.dp))
    }
}

// --- Pagination Controls ---

@Composable
fun ExpensePaginationControls(
    screenSize: ScreenSize,
    viewModel: ExpenseViewModel,
    paginationRequest: MutableState<PaginationRequest>
) {
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val page = state.value.page
    val totalPages = state.value.totalPages

    val onPageChange: (Long) -> Unit = { newPage ->
        debounceJob?.cancel()
        debounceJob = coroutineScope.launch {
            paginationRequest.value.page = newPage
            viewModel.getExpenses(paginationRequest.value)
        }
    }

    if (screenSize == ScreenSize.COMPACT) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onPageChange(page - 1L) },
                enabled = page > 1
            ) {
                Icon(Icons.Default.KeyboardArrowLeft, contentDescription = "Previous")
            }

            Text(
                text = "Page $page of $totalPages",
                style = MaterialTheme.typography.bodyMedium
            )

            IconButton(
                onClick = { onPageChange(page + 1L) },
                enabled = page < totalPages
            ) {
                Icon(Icons.Default.KeyboardArrowRight, contentDescription = "Next")
            }
        }
    } else {
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
                onClick = { onPageChange(page - 1L) },
                enabled = page > 1,
                text = "Previous"
            )
            PaginationButton(
                onClick = {},
                enabled = false,
                text = page.toString()
            )
            PaginationButton(
                onClick = { onPageChange(page + 1L) },
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

// --- Add Expense Dialog ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddExpenseDialog(
    staffId: String,
    viewModel: ExpenseViewModel,
    onDismiss: () -> Unit,
    onExpenseAdded: (ExpenseDto) -> Unit,
    userViewModel: UserViewModel
) {
    var description by remember { mutableStateOf("") }
    var amount by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var newCategory by remember { mutableStateOf("") }
    var isNewCategory by remember { mutableStateOf(false) }
    var paymentMethod by remember { mutableStateOf("") }

    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()
    val categories = state.value.expenseCategories

    var categoryExpanded by remember { mutableStateOf(false) }
    var paymentExpanded by remember { mutableStateOf(false) }
    val paymentOptions = listOf("Cash", "Card", "Transfer")

    var descriptionError by remember { mutableStateOf(false) }
    var amountError by remember { mutableStateOf(false) }
    var categoryError by remember { mutableStateOf(false) }
    var paymentError by remember { mutableStateOf(false) }

    fun validate(): Boolean {
        descriptionError = description.isBlank()
        amountError = amount.isBlank() || amount.toDoubleOrNull() == null
        categoryError = (category.isBlank() && !isNewCategory) || (isNewCategory && newCategory.isBlank())
        paymentError = paymentMethod.isBlank()
        return !descriptionError && !amountError && !categoryError && !paymentError
    }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Record New Expense") },
        text = {
            Column(
                modifier = Modifier.verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text("Description") },
                    isError = descriptionError,
                    singleLine = true
                )

                OutlinedTextField(
                    value = amount,
                    onValueChange = { amount = it },
                    label = { Text("Amount") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    isError = amountError,
                    singleLine = true
                )

                // Payment Method Dropdown
                ExposedDropdownMenuBox(
                    expanded = paymentExpanded,
                    onExpandedChange = { paymentExpanded = it }
                ) {
                    OutlinedTextField(
                        value = paymentMethod,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Payment Method") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = paymentError
                    )
                    ExposedDropdownMenu(
                        expanded = paymentExpanded,
                        onDismissRequest = { paymentExpanded = false }
                    ) {
                        paymentOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    paymentMethod = option
                                    paymentExpanded = false
                                }
                            )
                        }
                    }
                }

                // Category Dropdown
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                ) {
                    OutlinedTextField(
                        value = if (isNewCategory) "New Category..." else category,
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Category") },
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth(),
                        isError = categoryError
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("Add New...") },
                            onClick = {
                                isNewCategory = true
                                category = ""
                                categoryExpanded = false
                            }
                        )
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    category = option
                                    isNewCategory = false
                                    categoryExpanded = false
                                }
                            )
                        }
                    }
                }

                // New Category field
                AnimatedVisibility(visible = isNewCategory) {
                    OutlinedTextField(
                        value = newCategory,
                        onValueChange = { newCategory = it },
                        label = { Text("New Category Name") },
                        isError = categoryError
                    )
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (validate()) {
                        val finalCategory = if (isNewCategory) newCategory else category
                        val dto = ExpenseDto(
                            description = description,
                            amount = amount.toDouble(),
                            createdBy = staffId, // Pass the logged-in staff's ID
                            paymentMethod = paymentMethod.lowercase(),
                            category = finalCategory,
                            userName = userState.value.userName
                        )
                        onExpenseAdded(dto)
                        onDismiss()
                    }
                }
            ) {
                Text("Save")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}

// --- Filter Dialog ---

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ExpenseFilterDialog(
    currentRequest: PaginationRequest,
    viewModel: ExpenseViewModel,
    onDismiss: () -> Unit,
    onApplyFilters: (PaginationRequest) -> Unit,
    userViewModel: UserViewModel
) {
    val state = viewModel.state.collectAsStateWithLifecycle()
    val userState = userViewModel.state.collectAsStateWithLifecycle()
    val staffList = state.value.staffList
    val categories = state.value.expenseCategories // <-- New list

    // --- Temporary states ---
    var tempPeriod by remember { mutableStateOf(currentRequest.filter_by ?: "select::") }
    var tempStaffId by remember { mutableStateOf(currentRequest.staff_user_id) }
    var tempPaymentMethod by remember { mutableStateOf(currentRequest.payment_method) }
    var tempCategory by remember { mutableStateOf(currentRequest.category) } // <-- New state

    var tempStartDate by remember { mutableStateOf(currentRequest.start_date) }
    var tempEndDate by remember { mutableStateOf(currentRequest.end_date) }
    var tempDateRangeText by remember {
        mutableStateOf(
            if (!tempStartDate.isNullOrBlank() && !tempEndDate.isNullOrBlank()) {
                "${tempStartDate} - ${tempEndDate}"
            } else {
                "Select Date Range"
            }
        )
    }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // --- Dropdown states ---
    var paymentExpanded by remember { mutableStateOf(false) }
    val paymentOptions = listOf("cash", "card", "transfer")
    val defaultPaymentText = "All Payment Methods"
    var selectedPaymentText by remember(tempPaymentMethod) {
        mutableStateOf(tempPaymentMethod?.capitalize(Locale.ROOT) ?: defaultPaymentText)
    }

    var categoryExpanded by remember { mutableStateOf(false) } // <-- New state
    val defaultCategoryText = "All Categories"
    var selectedCategoryText by remember(tempCategory) {
        mutableStateOf(tempCategory ?: defaultCategoryText)
    }
    // --- Staff Dropdown States ---
    var staffDropdownExpanded by remember { mutableStateOf(false) }
    val defaultStaffText = "All Staff"
    var selectedStaffText by remember(tempStaffId) {
        mutableStateOf(
            staffList.find { it.userId == tempStaffId }?.name ?: defaultStaffText // TODO: Use name
        )
    }


    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Filter Expenses") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // --- 1. Filter by Period ---
                Text("Filter by Period", style = MaterialTheme.typography.titleSmall)
                PeriodDropdown(
                    selectedPeriod = tempPeriod,
                    onPeriodSelected = {
                        tempPeriod = it
                        tempStartDate = null
                        tempEndDate = null
                        tempDateRangeText = "Select Date Range"
                    }
                )

                // --- 2. Filter by Date Range ---
                Text("Filter by Date Range", style = MaterialTheme.typography.titleSmall)
                DatePickerField(
                    selectedDateRange = tempDateRangeText,
                    onDateRangeClick = { showDateRangePicker = true },
                    onDateRangeClear = {
                        tempStartDate = null
                        tempEndDate = null
                        tempDateRangeText = "Select Date Range"
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                if(userState.value.permissions.contains(SystemPermissions.VIEW_EXPENSES.id)) {
                    // --- 3. Filter by Staff ---
                    Text("Filter by Staff", style = MaterialTheme.typography.titleSmall)
                    // Re-using StaffDropdown
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

                // --- 4. Filter by Payment Method ---
                Text("Filter by Payment Method", style = MaterialTheme.typography.titleSmall)
                ExposedDropdownMenuBox(
                    expanded = paymentExpanded,
                    onExpandedChange = { paymentExpanded = it }
                ) {
                    OutlinedTextField(
                        value = selectedPaymentText,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = paymentExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = paymentExpanded,
                        onDismissRequest = { paymentExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(defaultPaymentText) },
                            onClick = {
                                selectedPaymentText = defaultPaymentText
                                paymentExpanded = false
                                tempPaymentMethod = null
                            }
                        )
                        paymentOptions.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option.capitalize(Locale.ROOT)) },
                                onClick = {
                                    selectedPaymentText = option.capitalize(Locale.ROOT)
                                    paymentExpanded = false
                                    tempPaymentMethod = option
                                }
                            )
                        }
                    }
                }

                // --- 5. NEW Filter by Category ---
                Text("Filter by Category", style = MaterialTheme.typography.titleSmall)
                ExposedDropdownMenuBox(
                    expanded = categoryExpanded,
                    onExpandedChange = { categoryExpanded = it }
                )
                {
                    OutlinedTextField(
                        value = selectedCategoryText,
                        onValueChange = {}, readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = categoryExpanded) },
                        modifier = Modifier.menuAnchor().fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = categoryExpanded,
                        onDismissRequest = { categoryExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text(defaultCategoryText) },
                            onClick = {
                                selectedCategoryText = defaultCategoryText
                                categoryExpanded = false
                                tempCategory = null
                            }
                        )
                        categories.forEach { option ->
                            DropdownMenuItem(
                                text = { Text(option) },
                                onClick = {
                                    selectedCategoryText = option
                                    categoryExpanded = false
                                    tempCategory = option
                                }
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text("Show:")
                    NumberPicker(
                        value = currentRequest.limit,
                        onValueChange = { currentRequest.limit = it },
                        modifier = Modifier.width(100.dp)
                    )
                    Text("Entries")
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalRequest = currentRequest.copy(
                        filter_by = tempPeriod.takeIf { it != "select::" },
                        staff_user_id = tempStaffId,
                        payment_method = tempPaymentMethod,
                        category = tempCategory, // <-- Add new filter
                        start_date = tempStartDate,
                        end_date = tempEndDate
                    )

                    if (finalRequest.start_date != null) {
                        finalRequest.filter_by = null
                    }

                    onApplyFilters(finalRequest)
                    onDismiss()
                }
            ) { Text("Done") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    val clearRequest = PaginationRequest(limit = currentRequest.limit)
                    onApplyFilters(clearRequest)
                    onDismiss()
                }
            ) { Text("Clear All Filters") }
        }
    )

    // --- Date Picker Modal ---
    if (showDateRangePicker) {
        LocalDateRangePickerModal(
            onDismiss = { showDateRangePicker = false },
            onDateRangeSelected = { startDate, endDate, text ->
                tempStartDate = startDate
                tempEndDate = endDate
                tempDateRangeText = text
                tempPeriod = "select::"
            }
        )
    }
}


// --- SHARED HELPER COMPOSABLES ---
// (These are assumed to exist or be copied from your Transaction UI)

// @Composable
// fun TableCell(text: String, modifier: Modifier, ...)

// @Composable
// fun PaginationButton(onClick: () -> Unit, text: String, ...)

// @Composable
// fun NumberPicker(paginationRequest: MutableState<PaginationRequest>, ...)

// @Composable
// fun DatePickerField(selectedDateRange: String, ...)

// @Composable
// fun LocalDateRangePickerModal(onDismiss: () -> Unit, onDateRangeSelected: (...) -> Unit)

// @Composable
// fun PeriodDropdown(selectedPeriod: String, onPeriodSelected: (String) -> Unit)

// @Composable
// fun StaffDropdown(selectedStaffId: String?, staffList: List<Staff>, ...)

// fun formatDate(timestamp: Long): String { ... }
// fun formatDateFilter(timestamp: Long): String { ... }

// object Util {
//     fun currencyFormat(amount: Double): String { ... }
// }

// enum class ScreenSize { COMPACT, EXPANDED }