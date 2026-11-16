package com.sundroid.kobokobo.dashboard.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionHistoryViewModel
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionScreen

@Composable
fun SalesMainScreen(
    dashboardViewModel: DashboardViewModel,
    transactionHistoryViewModel: TransactionHistoryViewModel,
    userViewModel: UserViewModel
) {
    var selectedTabIndex by remember { mutableStateOf(0) }
    val tabs = listOf("Sales (POS)", "Sales Report")

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        topBar = {
            TabRow(
                selectedTabIndex = selectedTabIndex,
                containerColor = MaterialTheme.colorScheme.surface,
                contentColor = MaterialTheme.colorScheme.primary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTabIndex]),
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTabIndex == index,
                        onClick = { selectedTabIndex = index },
                        text = { Text(text = title) },
                        selectedContentColor = MaterialTheme.colorScheme.primary,
                        unselectedContentColor = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when (selectedTabIndex) {
                0 -> {
                    POScreen(
                        viewModel = dashboardViewModel,
                        userViewModel = userViewModel
                    )
                }
                1 -> {
                    TransactionScreen(
                        viewModel = transactionHistoryViewModel,
                        userViewModel = userViewModel
                    )
                }
            }
        }
    }
}
