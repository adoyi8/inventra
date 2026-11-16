package com.sundroid.kobokobo.hotel.presentation

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.admin.presentation.RoomManagementScreen
import com.sundroid.kobokobo.hotel.presentation.components.BookingHistoryScreen
import com.sundroid.kobokobo.hotel.presentation.components.ReceptionistDashboardScreen
import com.sundroid.kobokobo.login.presentation.UserViewModel
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Composable
fun HotelMainScreen(
    businessId: String,
    staffId: String,
    userViewModel: UserViewModel,
    viewModel: HotelViewModel = koinViewModel { parametersOf(businessId, staffId) }
) {
    var selectedTab by remember { mutableStateOf(0) }
    val state by viewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()
    
    // Build the list of tabs based on user permissions
    val availableTabs = buildList {
        add(TabInfo("Front Desk", TabType.FRONT_DESK))
        if (userState.permissions.contains(SystemPermissions.MANAGE_ROOMS.id)) {
            add(TabInfo("Room Management", TabType.ROOM_MANAGEMENT))
        }
        add(TabInfo("History", TabType.HISTORY))
    }

    Scaffold { padding ->
        Column(modifier = Modifier.padding(padding).fillMaxSize()) {
            TabRow(selectedTabIndex = selectedTab) {
                availableTabs.forEachIndexed { index, tabInfo ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = { Text(tabInfo.title) }
                    )
                }
            }

            // Display the selected tab's content
            when (availableTabs.getOrNull(selectedTab)?.type) {
                TabType.FRONT_DESK -> ReceptionistDashboardScreen(viewModel = viewModel, state = state)
                TabType.ROOM_MANAGEMENT -> RoomManagementScreen(viewModel = viewModel, state = state)
                TabType.HISTORY -> BookingHistoryScreen(viewModel = viewModel, state = state, userViewModel = userViewModel)
                null -> {} // Fallback for invalid selection
            }
        }
    }
}

// Helper data class and enum for tab management
private data class TabInfo(val title: String, val type: TabType)
private enum class TabType { FRONT_DESK, ROOM_MANAGEMENT, HISTORY }
