package com.sundroid.kobokobo.services.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.material.icons.filled.ArrowDropUp
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.core.presentation.components.CustomText

import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.services.data.domain.model.ServicesCartItem
import com.sundroid.kobokobo.services.data.domain.model.ServicesMaterial

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesPOSScreen(
    ServicesViewModel: ServicesMaterialViewModel,
    posViewModel: ServicesPOSViewModel,
    userViewModel: UserViewModel,
    onNavigateToHistory: () -> Unit = {}
) {
    val ServicesState by ServicesViewModel.state.collectAsState()
    val posState by posViewModel.state.collectAsState()
    val userState by userViewModel.state.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomText("Services Point of Sale") },
                actions = {
                    val hasHistoryPermission = userState.permissions.contains(SystemPermissions.BOOK_APPOINTMENT.id) ||
                            userState.permissions.contains(SystemPermissions.COMPLETE_APPOINTMENT.id)
                    if (hasHistoryPermission) {
                        IconButton(onClick = onNavigateToHistory) {
                            Icon(Icons.Default.History, contentDescription = "History")
                        }
                    }
                }
            )
        },
        floatingActionButton = {
            BadgedBox(
                badge = {
                    if (posState.cartItems.isNotEmpty()) {
                        Badge { Text(posState.cartItems.size.toString()) }
                    }
                }
            ) {
                FloatingActionButton(onClick = { posViewModel.toggleCartSheet(true) }) {
                    Icon(Icons.Default.ShoppingCart, contentDescription = "View Cart")
                }
            }
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            if (ServicesState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (ServicesState.materials.isEmpty()) {
                CustomText(
                    "No Services services available. Add them from Admin Panel.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(ServicesState.materials) { material ->
                        ServicesServiceCard(
                            material = material,
                            onAddToCart = { qty ->
                                val cartItem = ServicesCartItem(
                                    materialId = material.id,
                                    materialName = material.name,
                                    quantity = qty,
                                    unitPrice = material.price
                                )
                                posViewModel.addItemToCart(cartItem)
                            }
                        )
                    }
                }
            }
        }
    }

    if (posState.showCartSheet) {
        ServicesCartBottomSheet(
            state = posState,
            userPermissions = userState.permissions,
            onDismiss = { posViewModel.toggleCartSheet(false) },
            onUpdateQuantity = { item, newQty -> posViewModel.updateItemQuantity(item, newQty) },
            onCheckout = { posViewModel.checkout() },
            onPaymentMethodSelected = { posViewModel.setPaymentMethod(it) },
            onProceedWithoutPaymentChanged = { posViewModel.setProceedWithoutPayment(it) },
            onStaffSelected = { posViewModel.setAssignedStaff(it) }
        )
    }

    if (posState.showCheckoutSuccess) {
        AlertDialog(
            onDismissRequest = { posViewModel.dismissSuccessDialog() },
            title = { CustomText("Success") },
            text = { CustomText("Services transaction completed successfully!") },
            confirmButton = {
                TextButton(onClick = { posViewModel.dismissSuccessDialog() }) {
                    CustomText("OK")
                }
            }
        )
    }
}

@Composable
fun ServicesServiceCard(material: ServicesMaterial, onAddToCart: (Int) -> Unit) {
    var quantity by remember { mutableStateOf(1) }

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).clickable(
                onClick = {
                    onAddToCart(quantity)
                    quantity = 1
                }
            ) .fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CustomText(material.name, fontWeight = FontWeight.Bold)
                CustomText(
                    "${Util.CURRENCY_CODE}${material.price}",
                    style = MaterialTheme.typography.bodyMedium.fontSize
                )
            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ServicesCartBottomSheet(
    state: ServicesPOSState,
    userPermissions: List<String>,
    onDismiss: () -> Unit,
    onUpdateQuantity: (ServicesCartItem, Int) -> Unit,
    onCheckout: () -> Unit,
    onPaymentMethodSelected: (String) -> Unit,
    onProceedWithoutPaymentChanged: (Boolean) -> Unit,
    onStaffSelected: (Staff?) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomText("Services Cart", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            
            if (state.cartItems.isEmpty()) {
                CustomText("Cart is empty", modifier = Modifier.align(Alignment.CenterHorizontally).padding(32.dp))
            } else {
                LazyColumn(modifier = Modifier.weight(1f, fill = false)) {
                    items(state.cartItems) { item ->
                        Row(
                            modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                CustomText(item.materialName, fontWeight = FontWeight.Bold)
                                CustomText("${Util.CURRENCY_CODE}${item.unitPrice} x ${item.quantity}")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onUpdateQuantity(item, item.quantity - 1) }) {
                                    Icon(Icons.Default.Remove, contentDescription = "Decrease")
                                }
                                CustomText(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(onClick = { onUpdateQuantity(item, item.quantity + 1) }) {
                                    Icon(Icons.Default.Add, contentDescription = "Increase")
                                }
                            }
                        }
                        HorizontalDivider()
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Assign To Dropdown ---
                CustomText(
                    text = "Assign To",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = state.assignedStaffName ?: "Select Staff",
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = {
                            Icon(
                                if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown,
                                contentDescription = null
                            )
                        },
                        modifier = Modifier.fillMaxWidth().menuAnchor(),
                        shape = RoundedCornerShape(12.dp)
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("None") },
                            onClick = {
                                onStaffSelected(null)
                                expanded = false
                            }
                        )
                        state.staffList.forEach { staff ->
                            DropdownMenuItem(
                                text = { Text(staff.name) },
                                onClick = {
                                    onStaffSelected(staff)
                                    expanded = false
                                }
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // --- Payment Method Selector ---
                CustomText(
                    text = "Payment Method",
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                val scrollState = rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayedMethods = state.paymentMethods.ifEmpty {
                        listOf(PaymentMethod("cash", "Cash"))
                    }

                    displayedMethods.forEach { method ->
                        val isSelected = state.selectedPaymentMethod.equals(method.id, ignoreCase = true) ||
                                state.selectedPaymentMethod.equals(method.name, ignoreCase = true)

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                onPaymentMethodSelected(method.id.ifEmpty { method.name })
                            }
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = {
                                    onPaymentMethodSelected(method.id.ifEmpty { method.name })
                                }
                            )
                            CustomText(
                                text = method.name,
                                style = MaterialTheme.typography.bodyMedium.fontSize,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp)
                        .clickable { onProceedWithoutPaymentChanged(!state.proceedWithoutPayment) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Checkbox(
                        checked = state.proceedWithoutPayment,
                        onCheckedChange = onProceedWithoutPaymentChanged
                    )
                    CustomText(
                        text = "Proceed without payment",
                        style = MaterialTheme.typography.bodyMedium.fontSize
                    )
                }
                
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    CustomText("Total:", fontWeight = FontWeight.Bold)
                    CustomText("${Util.CURRENCY_CODE}${state.totalAmount}", fontWeight = FontWeight.Bold)
                }

                if (state.isCheckoutLoading) {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally))
                } else {
                    val hasBookPermission = userPermissions.contains(SystemPermissions.BOOK_APPOINTMENT.id)
                    Button(
                        onClick = { onCheckout() },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(12.dp),
                        enabled = hasBookPermission
                    ) {
                        CustomText(if (hasBookPermission) "Checkout" else "Missing Permission", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
