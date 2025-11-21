package com.ikemba.inventrar.transactionHistory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
//import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.Image
//import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
//import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.cart.presentation.components.ReceiptDialog
import com.ikemba.inventrar.core.presentation.CustomGrey
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.core.presentation.components.PaginationButton
import com.ikemba.inventrar.core.presentation.components.ProgressDialog
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import inventrar.composeapp.generated.resources.eye_icon
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



@Composable
fun SearchBar(
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    paginationRequest: MutableState<PaginationRequest>
) {
    val query = remember { mutableStateOf("") }
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    Box(
        modifier = Modifier.fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            BasicTextField(
                value = query.value,
                onValueChange = {
                    query.value = it

                    debounceJob?.cancel()
                    viewModel.hideAllDialogs()
                    debounceJob = coroutineScope.launch {
                        delay(1500)
                        viewModel.searchTransactionHistory(it, paginationRequest)
                    }

                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun TransactionTable(viewModel: TransactionHistoryViewModel = koinViewModel()) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxWidth()

    ) {
        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)

        Box(
            modifier = Modifier.fillMaxSize().verticalScroll(stateVertical)
                .horizontalScroll(stateHorizontal)
        ) {

            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 3000.dp, min = 0.dp)) {
                items(state.value.transactionHistory.orders!!) { transaction ->
                    val index = state.value.transactionHistory.orders!!.indexOf(transaction)
                    Row(
                        modifier = Modifier.fillMaxWidth(0.8f)
                            .padding(horizontal = 19.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        TableCell(
                            (state.value.transactionHistory.limit * (state.value.transactionHistory.page - 1) + index + 1).toString(),
                            modifier = Modifier.width(200.dp)
                        )
                        Column(
                            modifier = Modifier.width(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText("Order No")
                            CustomText(transaction.orderId)
                        }
                        Column(
                            modifier = Modifier.width(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText(transaction.created)
                            CustomText(
                                Util.currencyFormat(transaction.totalAmount.toDouble()),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Column(
                            modifier = Modifier.width(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText("Payment Method")
                            CustomText(transaction.paymentMethod, fontWeight = FontWeight.Bold)
                        }
                        Column(
                            modifier = Modifier.width(200.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Image(
                                painter = painterResource(Res.drawable.eye_icon),
                                contentDescription = "",
                                modifier = Modifier.size(30.dp).clickable(
                                    onClick = {
                                        viewModel.getReceipt(transaction.orderId)
                                    }))
                        }
                    }
                }
            }
        }
//        VerticalScrollbar(
//            modifier = Modifier.align(Alignment.CenterEnd)
//                .fillMaxHeight(),
//            adapter = rememberScrollbarAdapter(stateVertical)
//        )
//        HorizontalScrollbar(
//            modifier = Modifier.align(Alignment.BottomStart)
//                .fillMaxWidth()
//                .padding(end = 12.dp),
//            adapter = rememberScrollbarAdapter(stateHorizontal)
//        )
    }
}



@Composable
fun TableCell(text: String, modifier: Modifier) {
    CustomText(text, size = 14.sp, modifier = modifier)
}

@Composable
fun PaginationControls(
    viewModel: TransactionHistoryViewModel, paginationRequest: MutableState<PaginationRequest>
) {
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle()
    val spacerWidth = 0.dp
    Row(
        modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
    ) {
        PaginationButton(onClick = {
            debounceJob?.cancel()
            viewModel.hideAllDialogs()
            debounceJob = coroutineScope.launch {
                paginationRequest.value.page = 1
                viewModel.getTransactionHistory(paginationRequest.value)
            }

        }, enabled = state.value.page > 1, text = "First")
        Spacer(modifier = Modifier.width(spacerWidth))
        PaginationButton(onClick = {
            debounceJob?.cancel()
            viewModel.hideAllDialogs()
            debounceJob = coroutineScope.launch {
                paginationRequest.value.page -= 1
                viewModel.getTransactionHistory(paginationRequest.value)
            }
        }, enabled = state.value.page > 1, text = "Previous")
        Spacer(modifier = Modifier.width(spacerWidth))
        PaginationButton(onClick = {}, enabled = false, text = state.value.page.toString())
        Spacer(modifier = Modifier.width(spacerWidth))
        PaginationButton(onClick = {
            debounceJob?.cancel()
            viewModel.hideAllDialogs()
            debounceJob = coroutineScope.launch {
                paginationRequest.value.page += 1
                viewModel.getTransactionHistory(paginationRequest.value)
            }
        }, text = "Next", enabled = true)
        Spacer(modifier = Modifier.width(spacerWidth))
        PaginationButton(onClick = {
            debounceJob?.cancel()
            viewModel.hideAllDialogs()
            debounceJob = coroutineScope.launch {
                paginationRequest.value.page = state.value.totalPages!!.toLong()
                viewModel.getTransactionHistory(paginationRequest.value)
            }
        }, enabled = state.value.page < state.value.totalPages, text = "Last")
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownMenuSample(
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    paginationRequest: MutableState<PaginationRequest>,
    modifier: Modifier = Modifier
) {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("select::", "daily", "weekly", "monthly")
    var selectedOption by remember { mutableStateOf(options[0]) } // Default: "Weekly"
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val state = viewModel.state.collectAsStateWithLifecycle()
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = modifier,

        ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier.menuAnchor().width(170.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded, onDismissRequest = { expanded = false }) {
            options.forEach { option ->
                DropdownMenuItem(text = { Text(option) }, onClick = {
                    selectedOption = option
                    expanded = false
                    debounceJob?.cancel()
                    viewModel.hideAllDialogs()
                    debounceJob = coroutineScope.launch {

                        viewModel.selectFilter(option, paginationRequest)
                    }
                })
            }
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
    viewModel: TransactionHistoryViewModel = koinViewModel(),
    paginationRequest: MutableState<PaginationRequest>
) {

    val dateRangePickerState = rememberDateRangePickerState()
    var debounceJob by remember { mutableStateOf<Job?>(null) }
    val coroutineScope = rememberCoroutineScope()
    DatePickerDialog(onDismissRequest = onDismiss, confirmButton = {
        TextButton(
            onClick = {
                onDateRangeSelected(
                    Pair(
                        dateRangePickerState.selectedStartDateMillis,
                        dateRangePickerState.selectedEndDateMillis
                    )
                )
                debounceJob?.cancel()
                viewModel.hideAllDialogs()
                debounceJob = coroutineScope.launch {

                    viewModel.searchByDateRange(
                        startDate = formatDateFilter(
                            dateRangePickerState.selectedStartDateMillis!!
                        ),
                        endDate = formatDateFilter(dateRangePickerState.selectedEndDateMillis!!),
                        paginationRequest
                    )
                }
                onDismiss()
            }) {
            Text("OK")
        }
    }, dismissButton = {
        TextButton(onClick = onDismiss) {
            Text("Cancel")
        }
    }) {
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier.fillMaxWidth().height(500.dp).padding(16.dp)
        )
    }
}

fun formatDate(timestamp: Long): String {
    val sdf = SimpleDateFormat("dd MMMM", Locale.getDefault())
    return sdf.format(Date(timestamp))
}

fun formatDateFilter(timestamp: Long): String {
    val sdf = SimpleDateFormat("yyyy-MM-dd")
    return sdf.format(Date(timestamp))
}



