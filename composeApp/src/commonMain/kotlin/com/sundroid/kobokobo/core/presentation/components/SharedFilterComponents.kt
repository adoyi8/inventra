package com.sundroid.kobokobo.core.presentation.components

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
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
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.dashboard.utils.Util
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import org.jetbrains.compose.resources.painterResource

@Composable
fun DatePickerField(
    selectedDateRange: String,
    onDateRangeClick: () -> Unit,
    onDateRangeClear: () -> Unit,
    modifier: Modifier = Modifier,
    label: String = "Select Date"
) {
    OutlinedTextField(
        value = selectedDateRange,
        onValueChange = {},
        readOnly = true,
        label = { Text(label) },
        leadingIcon = {
            Icon(
                painter = painterResource(Res.drawable.calendar),
                contentDescription = "Select Date Range",
                modifier = Modifier.size(24.dp).clickable { onDateRangeClick() }
            )
        },
        trailingIcon = {
            if (selectedDateRange != "Select Date Range" && selectedDateRange.isNotEmpty()) {
                IconButton(onClick = onDateRangeClear) {
                    Icon(imageVector = Icons.Default.Clear, contentDescription = "Clear")
                }
            }
        },
        modifier = modifier.clickable { onDateRangeClick() },
        enabled = false, // Disable direct text input, handle click via Modifier
        colors = OutlinedTextFieldDefaults.colors(
            disabledTextColor = MaterialTheme.colorScheme.onSurface,
            disabledBorderColor = MaterialTheme.colorScheme.outline,
            disabledLabelColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledTrailingIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
            disabledLeadingIconColor = MaterialTheme.colorScheme.onSurfaceVariant
        )
    )
    // Overlay box to capture clicks if enabled=false blocks modifier clicks (it often does for TextField)
    Box(modifier
        .clickable { onDateRangeClick() })
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PeriodDropdown(
    selectedPeriod: String,
    onPeriodSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val periods = listOf("Today", "Yesterday", "This Week", "Last Week", "This Month", "Last Month", "This Year", "Last Year")

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = selectedPeriod.removePrefix("select::").replaceFirstChar { it.uppercase() }, // Simple cleanup if needed
            onValueChange = {},
            readOnly = true,
            label = { Text("Period") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            colors = OutlinedTextFieldDefaults.colors(),
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            periods.forEach { period ->
                DropdownMenuItem(
                    text = { Text(period) },
                    onClick = {
                        onPeriodSelected(period)
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
    defaultText: String = "All Staff",
    onExpandedChange: (Boolean) -> Unit,
    onStaffSelected: (Staff?) -> Unit
) {
    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = onExpandedChange
    ) {
        OutlinedTextField(
            value = selectedText,
            onValueChange = {},
            readOnly = true,
            label = { Text("Staff") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { onExpandedChange(false) }
        ) {
            DropdownMenuItem(
                text = { Text(defaultText) },
                onClick = {
                    onStaffSelected(null)
                    onExpandedChange(false)
                }
            )
            staffList.forEach { staff ->
                DropdownMenuItem(
                    text = { Text(staff.name) }, // Use name or email
                    onClick = {
                        onStaffSelected(staff)
                        onExpandedChange(false)
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocalDateRangePickerModal(
    onDismiss: () -> Unit,
    onDateRangeSelected: (String?, String?, String) -> Unit
) {
    val datePickerState = rememberDateRangePickerState()
    val confirmEnabled = datePickerState.selectedStartDateMillis != null && datePickerState.selectedEndDateMillis != null

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    val start = datePickerState.selectedStartDateMillis?.let { Util.formatDate(it) }
                    val end = datePickerState.selectedEndDateMillis?.let { Util.formatDate(it) }
                    val text = if (start != null && end != null) "$start - $end" else "Select Date Range"
                    onDateRangeSelected(start, end, text)
                    onDismiss()
                },
                enabled = confirmEnabled
            ) {
                Text("OK")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    ) {
        DateRangePicker(state = datePickerState)
    }
}

@Composable
fun NumberPicker(
    value: Int,
    onValueChange: (Int) -> Unit,
    modifier: Modifier = Modifier,
    label: String = ""
) {
    var selectedOption by remember(value) { mutableStateOf(value.toString()) }

    OutlinedTextField(
        placeholder = { Text(label) },
        value = selectedOption,
        onValueChange = {
            try {
                if (it.isNotEmpty()) {
                    val newVal = it.toInt()
                    if (newVal > 0) {
                        selectedOption = it
                        onValueChange(newVal)
                    }
                } else {
                    selectedOption = ""
                }
            } catch (e: Exception) {
                // Ignore non-numeric input
            }
        },
        trailingIcon = {
            androidx.compose.foundation.layout.Column {
                Icon(
                    imageVector = Icons.Default.KeyboardArrowUp,
                    contentDescription = "Increase",
                    modifier = Modifier.clickable {
                        try {
                            val newVal = (selectedOption.toIntOrNull() ?: 0) + 1
                            selectedOption = newVal.toString()
                            onValueChange(newVal)
                        } catch (e: Exception) {}
                    }
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = "Decrease",
                    modifier = Modifier.clickable {
                        try {
                            val current = selectedOption.toIntOrNull() ?: 0
                            if (current > 1) {
                                val newVal = current - 1
                                selectedOption = newVal.toString()
                                onValueChange(newVal)
                            }
                        } catch (e: Exception) {}
                    }
                )
            }
        },
        modifier = modifier.fillMaxWidth(),
        singleLine = true
    )
}
