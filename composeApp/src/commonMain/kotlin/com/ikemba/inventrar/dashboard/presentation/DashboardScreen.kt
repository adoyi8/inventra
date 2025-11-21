

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
//import androidx.compose.foundation.TooltipArea
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.cart.presentation.CartScreen
import com.ikemba.inventrar.cart.presentation.components.ConfirmTransactionDialog
import com.ikemba.inventrar.cart.presentation.components.PaymentMethodNotAvailableDialog
import com.ikemba.inventrar.cart.presentation.components.PostingSalesDialog
import com.ikemba.inventrar.cart.presentation.components.ReceiptDialog
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.presentation.components.ChangePasswordDialog
import com.ikemba.inventrar.dashboard.presentation.components.ConfirmLogout
import com.ikemba.inventrar.dashboard.presentation.components.POSSCreenBody
import com.ikemba.inventrar.dashboard.presentation.components.SavingSalesDialog
import com.ikemba.inventrar.heldOrder.presentation.HeldOrderScreen
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.savedSales.presentation.SavedSalesScreen
import com.ikemba.inventrar.transactionHistory.presentation.TransactionScreen


@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun POScreen(viewModel: DashboardViewModel, userViewModel: UserViewModel) {

    val state = viewModel.state.collectAsStateWithLifecycle()

    // --- New Responsive & Sheet State ---
    val sheetState = rememberModalBottomSheetState()

    // --- End New State ---

    BoxWithConstraints {
        val isMobile = true
        Scaffold() { padding ->
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
                                modifier = Modifier.fillMaxSize(),
                                isMobile = true,
                                onShowCart = { viewModel.showCartSheet(true) } // Lambda to open sheet
                            )
                        } else {
                            // Desktop: Side-by-side layout
                            Row(modifier = Modifier.fillMaxWidth()) {
                                POSSCreenBody(
                                    viewModel = viewModel,
                                    modifier = Modifier.weight(12f),
                                    isMobile = false,
                                    onShowCart = {} // Not needed on desktop
                                )
                                CartScreen(viewModel = viewModel, modifier = Modifier.weight(8f))
                            }
                        }
                        // --- END RESPONSIVE LOGIC ---

                        // Dialogs remain the same...
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
                ConfirmLogout(viewModel = userViewModel)
                ChangePasswordDialog(viewModel = viewModel)
                SavingSalesDialog(viewModel = viewModel)

            }
            SnackbarHost(hostState = state.value.snackBarHostState, modifier = Modifier.align(Alignment.Center))

            // --- New Modal Bottom Sheet for Mobile Cart ---
            if (state.value.showCartSheet) {
                ModalBottomSheet(
                    onDismissRequest = { viewModel.showCartSheet(false) },
                    sheetState = sheetState
                ) {
                    // We can reuse the same CartScreen composable!
                    CartScreen(viewModel = viewModel, modifier = Modifier.fillMaxSize())
                }
            }
            // --- End New Sheet ---
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
                    size = 18.sp,
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
    val menuItems = listOf("POS", "Held", "Transactions", "Saved", "Settings")

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
                            else -> Icons.Default.Clear
                        },
                        contentDescription = title
                    )
                }
            )
        }
    }
}