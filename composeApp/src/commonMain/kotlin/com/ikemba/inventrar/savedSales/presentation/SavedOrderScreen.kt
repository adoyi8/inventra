package com.ikemba.inventrar.savedSales.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.MutableTransitionState
//import androidx.compose.foundation.HorizontalScrollbar
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
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DateRangePicker
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.rememberDateRangePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.CustomGrey
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.utils.Util
import com.ikemba.inventrar.transactionHistory.presentation.formatDate
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.calendar
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel

@Composable
fun SavedSalesScreen(viewModel: DashboardViewModel) {
    var selectedEntries by remember { mutableStateOf("10") }
    var selectedDateRange by remember { mutableStateOf("Select Date Range") }
    var showDateRangeDropDown = MutableTransitionState(false)
    var searchText by remember { mutableStateOf("") }

    val state = viewModel.state.collectAsStateWithLifecycle()


    Box(modifier = Modifier.fillMaxSize().background(CustomGrey)) {



        Column(modifier = Modifier.fillMaxSize().padding(24.dp).background(Color.Transparent)) {
            Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.SpaceBetween){
             Column(){
                 CustomText("Saved Orders", size = 24.sp, color = Color.Black)
                 CustomText("Here is a list of all saved orders", size = 14.sp, color = Color.Gray)
             }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), verticalAlignment = Alignment.CenterVertically){
                    HeldOrderDropdownMenuSample()
                    Row(modifier = Modifier.background(Color.White).clickable(
                        onClick = {
                            showDateRangeDropDown.targetState = true
                        }
                    )){
                        OutlinedTextField(
                            value = selectedDateRange,
                            onValueChange = {},
                            readOnly = true,
                            trailingIcon = {
                                Icon(
                                    painter =  painterResource(Res.drawable.calendar),
                                    contentDescription = "",
                                    modifier = Modifier.size(35.dp)
                                        .clickable(
                                            onClick = {
                                                showDateRangeDropDown.targetState = true
                                            }

                                        )
                                )
                            },
                            modifier = Modifier.clickable(
                                onClick = {showDateRangeDropDown.targetState = true}
                            )

                        )
                    }
                    AnimatedVisibility(
                        visibleState = showDateRangeDropDown
                    ) {
                        HeldOrderDateRangePickerModal(onDismiss = { showDateRangeDropDown.targetState = false },
                            onDateRangeSelected = {
                                val stringBuilder: StringBuilder = StringBuilder()
                                it.first?.let {
                                    stringBuilder.append(formatDate(it))

                                }
                                it.second?.let {
                                    stringBuilder.append(" - ")
                                    stringBuilder.append(formatDate(it))
                                }
                                selectedDateRange = stringBuilder.toString()
                            })
                    }
                }
            }


            Spacer(modifier = Modifier.height(20.dp))
            Column(modifier = Modifier.fillMaxSize().background(Color.White).padding(20.dp)) {

                Column(modifier = Modifier.fillMaxHeight(0.9f)) {
                    Spacer(modifier = Modifier.height(16.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            modifier = Modifier,
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(5.dp)
                        ) {
                            CustomText("show: ")
                            HeldOrderNumberPicker()
                            CustomText("Entries: ")
                        }
                        Row(modifier = Modifier.width(350.dp)) {
                            HeldOrderSearchBar(searchText) { searchText = it }
                        }

                    }

                    Spacer(modifier = Modifier.height(16.dp))



                    Spacer(modifier = Modifier.height(16.dp))

                    HeldOrderTransactionTable(viewModel = viewModel)

                    Spacer(modifier = Modifier.height(16.dp))
                }
                HeldOrderPaginationControls()
            }
        }
        if(state.value.savedSales.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize().padding(24.dp).background(Color.Transparent),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                CustomText("There are no Items to display")
            }
        }

    }
}



@Composable
fun HeldOrderSearchBar(searchText: String, onSearchTextChanged: (String) -> Unit) {
    Box(
        modifier = Modifier.fillMaxWidth()
            .background(Color.LightGray, RoundedCornerShape(8.dp))
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Search")
            BasicTextField(
                value = searchText,
                onValueChange = onSearchTextChanged,
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
                modifier = Modifier.fillMaxWidth().padding(start = 8.dp)
            )
        }
    }
}

@Composable
fun HeldOrderTransactionTable(viewModel: DashboardViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    Box(
        modifier = Modifier.fillMaxWidth()

    ) {
        val stateVertical = rememberScrollState(0)
        val stateHorizontal = rememberScrollState(0)

        Box(
            modifier = Modifier.fillMaxSize()
           .verticalScroll(stateVertical)
                .horizontalScroll(stateHorizontal)
        ) {


            LazyColumn(modifier = Modifier.fillMaxWidth().heightIn(max = 1800.dp, min = 0.dp)) {

                items(state.value.savedSales) { transaction ->
                    val index = state.value.savedSales.indexOf(transaction)
                    Row(
                        modifier = Modifier.fillMaxWidth()
                            .padding(horizontal = 19.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {

                        HeldOrderTableCell((index + 1).toString(), modifier = Modifier)
                        Column(
                            modifier = Modifier.width(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText("Order No")
                            CustomText(transaction.reference)
                        }
                        Column(
                            modifier = Modifier.width(300.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            CustomText(transaction.created)
                            CustomText(
                                Util.currencyFormat(transaction.grandTotal!!),
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Row(
                            modifier = Modifier.width(300.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.Center
                        ) {
                            Button({

                            }, colors = ButtonDefaults.buttonColors(containerColor = Color.Red)) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = Icons.Default.Close, contentDescription = ""
                                    )
                                    CustomText("Delete", color = Color.White)
                                }
                            }
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
fun HeldOrderTableHeader(text: String, modifier: Modifier) {
    CustomText(text, size = 14.sp, modifier = modifier)
}

@Composable
fun HeldOrderTableCell(text: String, modifier: Modifier) {
    CustomText(text, size = 14.sp, modifier = modifier)
}

@Composable
fun HeldOrderPaginationControls() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Button(onClick = { /* First */ }, enabled = false) { Text("First") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* Previous */ }, enabled = false) { Text("Previous") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* Current Page */ }) { Text("1") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* Next */ }) { Text("Next") }
        Spacer(modifier = Modifier.width(8.dp))
        Button(onClick = { /* Last */ }, enabled = false) { Text("Last") }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeldOrderDropdownMenuSample() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Daily", "Weekly", "Monthly")
    var selectedOption by remember { mutableStateOf(options[0]) } // Default: "Weekly"

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = it },
        modifier = Modifier.background(Color.White).width(170.dp),

    ) {
        OutlinedTextField(
            value = selectedOption,
            onValueChange = {},
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
            },
            modifier = Modifier
                .menuAnchor()
                .width(170.dp)
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        selectedOption = option
                        expanded = false
                    }
                )
            }
        }
    }
}



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeldOrderDateRangePickerModal(
    onDateRangeSelected: (Pair<Long?, Long?>) -> Unit,
    onDismiss: () -> Unit,
) {
    val dateRangePickerState = rememberDateRangePickerState()

    DatePickerDialog(
        onDismissRequest = onDismiss,
        confirmButton = {
            TextButton(
                onClick = {
                    onDateRangeSelected(
                        Pair(
                            dateRangePickerState.selectedStartDateMillis,
                            dateRangePickerState.selectedEndDateMillis
                        )
                    )
                    onDismiss()
                }
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
        DateRangePicker(
            state = dateRangePickerState,
            title = {
                Text(
                    text = "Select date range"
                )
            },
            showModeToggle = false,
            modifier = Modifier
                .fillMaxWidth()
                .height(500.dp)
                .padding(16.dp)
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeldOrderNumberPicker() {
    var expanded by remember { mutableStateOf(false) }
    val options = listOf("Daily", "Weekly", "Monthly")
    var selectedOption by remember { mutableStateOf("10") } // Default: "Weekly"


        OutlinedTextField(
            value = selectedOption,
            onValueChange = {
                try{
                    if(it.toInt()>0){
                        selectedOption = it
                    }
                }
                catch (e: Exception){

                }
            },
            readOnly = true,
            trailingIcon = {
                Column(){
                    Icon( imageVector = Icons.Default.KeyboardArrowUp,
                        contentDescription = "",
                        modifier =  Modifier.clickable(onClick = {
                            try{
                                selectedOption = (selectedOption.toInt() + 1).toString()
                            }
                            catch (e: Exception){

                            }
                        })
                    )
                    Icon( imageVector = Icons.Default.KeyboardArrowDown,
                        contentDescription = "",
                        modifier =  Modifier.clickable(onClick = {
                            try{
                                if(selectedOption.toInt()>0) {
                                    selectedOption = (selectedOption.toInt() - 1).toString()
                                }
                            }
                            catch (e: Exception){

                            }
                        })
                        )
                }
            },
            modifier = Modifier

                .width(90.dp).height(50.dp)
        )

}

