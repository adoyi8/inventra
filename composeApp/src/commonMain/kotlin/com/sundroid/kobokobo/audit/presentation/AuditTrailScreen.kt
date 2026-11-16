package com.sundroid.kobokobo.audit.presentation

import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailPaginationRequest
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.core.presentation.components.DatePickerField
import com.sundroid.kobokobo.core.presentation.components.LocalDateRangePickerModal
import com.sundroid.kobokobo.core.presentation.components.PaginationButton
import com.sundroid.kobokobo.core.presentation.components.PeriodDropdown
import com.sundroid.kobokobo.core.presentation.components.StaffDropdown
import com.sundroid.kobokobo.theme.TextSize.bodyMedium
import com.sundroid.kobokobo.theme.TextSize.bodySmall
import com.sundroid.kobokobo.theme.TextSize.titleSmall
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import java.text.SimpleDateFormat
import java.util.Locale

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun AuditTrailScreen(
    viewModel: AuditTrailViewModel = koinViewModel()
) {
    var showFilterDialog by remember { mutableStateOf(false) }
    // State to hold the current filter/pagination parameters
    val paginationRequest = remember { mutableStateOf(AuditTrailPaginationRequest()) }

    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold (
        floatingActionButton = {

            FloatingActionButton(
                onClick =  { showFilterDialog = true },
                modifier = Modifier
                    // 2. Apply a bottom padding/margin to shift the FAB upwards.
                    // Use a value large enough to clear a NavigationBar or similar element.
                    .padding(bottom = 64.dp),
            ) {
               Icon(imageVector = Icons.Default.FilterList, contentDescription = "filter")
            }
        }

    ){ padding ->
        BoxWithConstraints(
            modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp)
        ) {
            // Simple responsiveness check
            val screenSize = if (maxWidth < 600.dp) ScreenSize.COMPACT else ScreenSize.EXPANDED

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(if (screenSize == ScreenSize.COMPACT) 16.dp else 24.dp)
            ) {
                // --- Top Controls ---


                Spacer(modifier = Modifier.height(16.dp))

                // --- Audit Trail List ---
                AuditTrailList(
                    screenSize = screenSize,
                    auditTrails = state.value.auditTrails,
                    staffList = state.value.staffList,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // --- Pagination Controls ---
                // Re-use your generic PaginationControls composable
                AuditTrailPaginationControls(screenSize,viewModel, paginationRequest)
            }

            // --- Modals and Overlays ---

            // Empty State
            if (state.value.auditTrails.isEmpty() && !state.value.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CustomText("No audit trails found.")
                }
            }

            // Loading State
            if (state.value.isLoading) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // Error State
            if (state.value.errorMessage.isNotEmpty()) {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CustomText("Error: ${state.value.errorMessage}", color = MaterialTheme.colorScheme.error)
                }
            }


            // Filter Dialog
            if (showFilterDialog) {
                AuditTrailFilterDialog(
                    currentRequest = paginationRequest.value,
                    staffList = state.value.staffList,
                    onDismiss = { showFilterDialog = false },
                    onApplyFilters = { newRequest ->
                        paginationRequest.value = newRequest
                        viewModel.applyFilters(newRequest)
                    }
                )
            }
        }
    }
}

// Enum for responsiveness
enum class ScreenSize { COMPACT, EXPANDED }

@Composable
fun AuditTrailCard(auditTrail: AuditTrail) {
    val dateFormat = remember { SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault()) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {

                // --- FIX IS HERE ---
                auditTrail.createdAt?.let { date ->
                    CustomText(
                        text = dateFormat.format(date), // Pass the Date object directly
                        style = bodySmall
                    )
                } ?: CustomText(
                    text = "N/A", // Handle case where createdAt is null
                    style = bodySmall
                )
                // --- END FIX ---
            }

            Spacer(modifier = Modifier.height(8.dp))

            CustomText(
                text = auditTrail.auditTrailMessage,
                style = bodyMedium
            )

        }
    }
}

@Composable
fun AuditTrailList(
    screenSize: ScreenSize,
    auditTrails: List<AuditTrail>,
    staffList: List<Staff>,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier.fillMaxWidth(),
        contentPadding = PaddingValues(vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(auditTrails) { auditTrail ->
            // Find staff name for display
            val staffName = staffList.find { it.userId == auditTrail.staffId }?.roleId ?: auditTrail.staffId.take(8)

            // You can pass staffName to the card if you want to display it
            AuditTrailCard(auditTrail = auditTrail)
        }
    }
}

// Your existing AuditTrailCard is fine to use here.
// You might want to add the staff name to it for better context.
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditTrailFilterDialog(
    currentRequest: AuditTrailPaginationRequest,
    staffList: List<Staff>,
    onDismiss: () -> Unit,
    onApplyFilters: (AuditTrailPaginationRequest) -> Unit
) {
    // --- Temporary states for the dialog ---
    var tempPeriod by remember { mutableStateOf(currentRequest.filter_by ?: "select::") }
    var tempStaffId by remember { mutableStateOf(currentRequest.staff_user_id) }
    var tempActionType by remember { mutableStateOf(currentRequest.action_type) }
    var tempEntityType by remember { mutableStateOf(currentRequest.entity_type) }

    var tempStartDate by remember { mutableStateOf(currentRequest.start_date) }
    var tempEndDate by remember { mutableStateOf(currentRequest.end_date) }
    var tempDateRangeText by remember {
        mutableStateOf(
            if (!tempStartDate.isNullOrBlank() && !tempEndDate.isNullOrBlank()) "$tempStartDate - $tempEndDate"
            else "Select Date Range"
        )
    }
    var showDateRangePicker by remember { mutableStateOf(false) }

    // --- Dropdown states ---
    var actionDropdownExpanded by remember { mutableStateOf(false) }
    var entityDropdownExpanded by remember { mutableStateOf(false) }
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
        title = { CustomText("Filter Audit Trails") },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. Period Filter
                CustomText("Filter by Period", style = titleSmall)
                PeriodDropdown( // Re-use your generic component
                    selectedPeriod = tempPeriod,
                    onPeriodSelected = {
                        tempPeriod = it
                        tempStartDate = null; tempEndDate = null
                        tempDateRangeText = "Select Date Range"
                    }
                )

                // 2. Date Range Filter
                CustomText("Filter by Date Range", style = titleSmall)
                DatePickerField( // Re-use your generic component
                    selectedDateRange = tempDateRangeText,
                    onDateRangeClick = { showDateRangePicker = true },
                    onDateRangeClear = {
                        tempStartDate = null; tempEndDate = null
                        tempDateRangeText = "Select Date Range"
                    },
                    modifier = Modifier.fillMaxWidth()
                )

                // 3. Staff Filter
                CustomText("Filter by Staff", style = titleSmall)
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
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    CustomText("Show:")
                    // Re-use your generic NumberPicker
                    AuditTrailNumberPicker(
                        currentRequest
                    )
                    CustomText("Entries")
                }

            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    val finalRequest = currentRequest.copy(
                        filter_by = tempPeriod.takeIf { it != "select::" },
                        staff_user_id = tempStaffId,
                        action_type = tempActionType,
                        entity_type = tempEntityType,
                        start_date = tempStartDate,
                        end_date = tempEndDate
                    )
                    if (finalRequest.start_date != null) finalRequest.filter_by = null
                    onApplyFilters(finalRequest)
                    onDismiss()
                }
            ) { CustomText("Done") }
        },
        dismissButton = {
            TextButton(
                onClick = {
                    onApplyFilters(AuditTrailPaginationRequest(limit = currentRequest.limit))
                    onDismiss()
                }
            ) { CustomText("Clear All") }
        }
    )

    if (showDateRangePicker) {
        LocalDateRangePickerModal( // Re-use your generic component
            onDismiss = { showDateRangePicker = false },
            onDateRangeSelected = { startDate, endDate, text ->
                tempStartDate = startDate; tempEndDate = endDate
                tempDateRangeText = text; tempPeriod = "select::"
            }
        )
    }
}
@Composable
fun AuditTrailPaginationControls(
    screenSize: ScreenSize,
    viewModel: AuditTrailViewModel,
    paginationRequest: MutableState<AuditTrailPaginationRequest>
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
            viewModel.getAuditTrails(paginationRequest.value)
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
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuditTrailNumberPicker(
    paginationRequest: AuditTrailPaginationRequest,
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

}