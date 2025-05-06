

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.Surface
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
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
import com.ikemba.inventrar.dashboard.presentation.components.ProfileImage
import com.ikemba.inventrar.dashboard.presentation.components.SavingSalesDialog
import com.ikemba.inventrar.dashboard.presentation.components.TopMenu
import com.ikemba.inventrar.heldOrder.presentation.HeldOrderScreen
import com.ikemba.inventrar.savedSales.presentation.SavedSalesScreen
import com.ikemba.inventrar.transactionHistory.presentation.TransactionScreen
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.inventra_logo_and_text
import inventrar.composeapp.generated.resources.logout
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class, ExperimentalFoundationApi::class)
@Composable
fun POScreen(viewModel: DashboardViewModel) {


    val state by viewModel.state.collectAsStateWithLifecycle()

    LaunchedEffect(true){
//      state.selectedMenuIndex.currentState == 0
  //      viewModel.hideChangePasswordDialog()
    }
    Scaffold() {
        Box(modifier = Modifier.fillMaxSize()) {


            Column(modifier = Modifier.fillMaxSize()) {

                // Top Bar
                TopAppBar(
                    navigationIcon = {
                        Image(
                            painter = painterResource(Res.drawable.inventra_logo_and_text),
                            contentDescription = "Inventrar Logo",
                            colorFilter = ColorFilter.tint(Color.White),
                            modifier = Modifier.padding(10.dp)
                        )
                    },
                    title = {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {

                            TopMenu()


                            if (state.users.isNotEmpty()) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                                ) {
                                    ProfileImage(
                                        imageUrl = state.users.first().businessLogo,
                                        content = {}
                                    )
                                    Column {
                                        CustomText(
                                            state.users.first().getFullName(),
                                            color = Color.White
                                        )
                                    }
                                    TooltipArea(
                                        tooltip = {
                                            // composable tooltip content
                                            Surface(
                                                modifier = Modifier.shadow(4.dp),
                                                shape = RoundedCornerShape(4.dp)
                                            ) {
                                                CustomText(
                                                    text = "Logout",
                                                )
                                            }
                                        }, // in milliseconds
                                        // tooltip offset
                                    ) {
                                        Icon(
                                            painter = painterResource(Res.drawable.logout),
                                            contentDescription = "Logout",
                                            tint = Color.White,
                                            modifier = Modifier.size(43.dp).padding(start = 8.dp)
                                                .clickable(
                                                    onClick = { viewModel.showShowConfirmLogout() },
                                                    enabled = state.users.isNotEmpty()
                                                )
                                        )
                                    }
                                }
                            }

                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = Color(0xFF236ff9))
                )

                AnimatedVisibility(
                    visible = state.selectedMenuIndex.currentState == 0,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    Box(modifier = Modifier.fillMaxSize()) {
                        Row(modifier = Modifier.fillMaxWidth()) {

                            POSSCreenBody(state = state, modifier = Modifier.weight(12f))
                            CartScreen(modifier = Modifier.weight(8f))
                        }
                        ConfirmTransactionDialog(text = "Are you sure you want to proceed")
                        PaymentMethodNotAvailableDialog(text = "Are you sure you want to proceed")
                        PostingSalesDialog(text = "Please wait...")

                        val onDismiss = {
                            viewModel.toggleShowReceipt(false)
                        }
                        ReceiptDialog(state.showReceipt, onDismiss, state.receiptModel)
                    }
                }
                AnimatedVisibility(
                    visible = state.selectedMenuIndex.currentState == 1,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    HeldOrderScreen()
                }
                AnimatedVisibility(
                    visible = state.selectedMenuIndex.currentState == 2,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    TransactionScreen()
                }
                AnimatedVisibility(
                    visible = state.selectedMenuIndex.currentState == 3,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                    SavedSalesScreen()
                }
                AnimatedVisibility(
                    visible = state.selectedMenuIndex.currentState == 4,
                    enter = slideInHorizontally(),
                    exit = slideOutHorizontally()
                ) {
                SettingsScreen(viewModel)
                }
                ConfirmLogout(viewModel = viewModel)
                ChangePasswordDialog(viewModel= viewModel)
                SavingSalesDialog()

            }
            SnackbarHost(hostState = viewModel.state.value.snackBarHostState, modifier = Modifier.align(Alignment.Center))
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

