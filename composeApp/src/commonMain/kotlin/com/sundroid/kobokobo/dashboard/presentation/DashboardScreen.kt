package com.sundroid.kobokobo.dashboard.presentation

//import androidx.compose.foundation.TooltipArea
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.cart.presentation.CartScreen
import com.sundroid.kobokobo.cart.presentation.components.ConfirmTransactionDialog
import com.sundroid.kobokobo.cart.presentation.components.PaymentMethodNotAvailableDialog
import com.sundroid.kobokobo.cart.presentation.components.PostingSalesDialog
import com.sundroid.kobokobo.cart.presentation.components.ReceiptDialog
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.dashboard.presentation.components.ChangePasswordDialog
import com.sundroid.kobokobo.dashboard.presentation.components.ConfirmLogout
import com.sundroid.kobokobo.dashboard.presentation.components.POSSCreenBody
import com.sundroid.kobokobo.dashboard.presentation.components.SavingSalesDialog
import com.sundroid.kobokobo.heldOrder.presentation.HeldOrderScreen
import com.sundroid.kobokobo.hotel.presentation.HotelMainScreen
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.savedSales.presentation.SavedSalesScreen
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionScreen
import my.connectivity.kmp.rememberNetworkStatus


@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun POScreen(viewModel: DashboardViewModel, userViewModel: UserViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()
    val networkStatus by rememberNetworkStatus()

    // --- New Responsive & Sheet State ---
    val sheetState = rememberModalBottomSheetState()

    // --- End New State ---
    LaunchedEffect(true){
        viewModel.getProducts()
        if(viewModel.state.value.categories.isEmpty()){ // Optimization: fetch only if empty
            viewModel.getCategories()
        }
        viewModel.getPaymentMethods()
    }
    
    BoxWithConstraints {
        // Simple breakpoint logic
        val isMobile = maxWidth < 900.dp 
        
        Scaffold(
            containerColor = MaterialTheme.colorScheme.background
        ) { padding ->
            Box(modifier = Modifier.padding(padding).fillMaxSize()) {

                // --- POS View (Index 0) ---
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 0,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {

                        if (isMobile) {
                            POSSCreenBody(
                                viewModel = viewModel,
                                userViewModel = userViewModel,
                                modifier = Modifier.fillMaxSize(),
                                isMobile = true,
                                onShowCart = { viewModel.showCartSheet(true) } // Lambda to open sheet
                            )
                        } else {
                            // Desktop: Side-by-side layout
                            Row(modifier = Modifier.fillMaxWidth()) {
                                POSSCreenBody(
                                    viewModel = viewModel,
                                    userViewModel = userViewModel,
                                    modifier = Modifier.weight(0.6f), // 60% width
                                    isMobile = false,
                                    onShowCart = {} 
                                )
                                // Divider/Border
                                Box(modifier = Modifier.width(1.dp).fillMaxSize())
                                
                                CartScreen(viewModel = viewModel, userViewModel= userViewModel, modifier = Modifier.weight(0.4f)) // 40% width
                            }
                        }
                        
                        // Dialogs
                        ConfirmTransactionDialog(viewModel = viewModel, text = "Are you sure you want to proceed")
                        PaymentMethodNotAvailableDialog(viewModel = viewModel, text = "Are you sure you want to proceed")
                        PostingSalesDialog(viewModel = viewModel, text = "Please wait...")
                        val onDismiss = { viewModel.toggleShowReceipt(false) }
                        ReceiptDialog(state.value.showReceipt, onDismiss, state.value.receiptModel)
                    }
                }

                // Other AnimatedVisibility screens
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 1,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    HeldOrderScreen(dashboardViewModel = viewModel)
                }
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 2,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    TransactionScreen(userViewModel= userViewModel)
                }
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 3,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    SavedSalesScreen(viewModel = viewModel)
                }
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 4,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    SettingsScreen(viewModel)
                }
                AnimatedVisibility(
                    visible = state.value.selectedMenuIndex.currentState == 5,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    val user = state.value.users.firstOrNull()
                    if (user != null) {
                        HotelMainScreen(
                            businessId = user.businessId ?: "",
                            staffId = user.id,
                            userViewModel = userViewModel
                        )
                    } else {
                        // Fallback or loading if user not ready
                        Box(Modifier.fillMaxSize())
                    }
                }
                ConfirmLogout(viewModel = userViewModel)
                ChangePasswordDialog(viewModel = viewModel)
                SavingSalesDialog(viewModel = viewModel)

            }
            SnackbarHost(hostState = state.value.snackBarHostState, modifier = Modifier.align(Alignment.Center))

            // --- New Modal Bottom Sheet for Mobile Cart ---
            if (state.value.showCartSheet && isMobile) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.showCartSheet(false) },
                    sheetState = sheetState,
                    containerColor = MaterialTheme.colorScheme.surface
                ) {
                    CartScreen(viewModel = viewModel, userViewModel = userViewModel,modifier = Modifier.fillMaxSize())
                }
            }
        }
    }
}



@Composable
fun SettingsScreen(viewModel: DashboardViewModel) {
    Scaffold(topBar = {
    }) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            Card(
                modifier = Modifier
                    .width(250.dp)
                    .clickable { viewModel.showChangePasswordDialog() },
                shape = MaterialTheme.shapes.medium,
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
            ) {
                CustomText(
                    text = "Change Password",
                    style = 18.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}

@Composable
fun MobileBottomNavigation(viewModel: DashboardViewModel, selectedIndex: Int) {
    // Assuming you have a list of items for your TopMenu
    val menuItems = listOf("POS", "Held", "Transactions", "Saved", "Settings", "Hotel")

    NavigationBar {
        menuItems.forEachIndexed { index, title ->
            NavigationBarItem(
                selected = selectedIndex == index,
                onClick = { viewModel.onMenuSelected(index) },
                label = { CustomText(title) }, // Use your CustomText
                icon = {
                    // Replace with your actual icons
                    Icon(
                        imageVector = when (index) {
                            0 -> Icons.Default.Menu
                            1 -> Icons.Default.Star
                            2 -> Icons.Default.Star
                            3 -> Icons.Default.Clear
                            4 -> Icons.Default.ArrowDropDown
                            5 -> Icons.Default.Home // Hotel Icon
                            else -> Icons.Default.Clear
                        },
                        contentDescription = title
                    )
                }
            )
        }
    }
}