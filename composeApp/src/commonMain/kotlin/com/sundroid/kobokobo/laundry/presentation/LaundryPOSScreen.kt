package com.sundroid.kobokobo.laundry.presentation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryCartItem
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryMaterial
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.dashboard.utils.Util

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryPOSScreen(
    laundryViewModel: LaundryMaterialViewModel,
    posViewModel: LaundryPOSViewModel,
    userViewModel: UserViewModel,
    onNavigateToHistory: () -> Unit = {}
) {
    val laundryState by laundryViewModel.state.collectAsState()
    val posState by posViewModel.state.collectAsState()
    
    var selectedMaterial by remember { mutableStateOf<LaundryMaterial?>(null) }
    var showServiceDialog by remember { mutableStateOf(false) }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { CustomText("Laundry Point of Sale") },
                actions = {
                    IconButton(onClick = onNavigateToHistory) {
                        Icon(Icons.Default.History, contentDescription = "History")
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
            if (laundryState.isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (laundryState.materials.isEmpty()) {
                CustomText(
                    "No laundry materials available. Add them from Admin Panel.",
                    modifier = Modifier.align(Alignment.Center)
                )
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(laundryState.materials) { material ->
                        LaundryMaterialCard(material = material) {
                            selectedMaterial = material
                            showServiceDialog = true
                        }
                    }
                }
            }
        }
    }

    if (showServiceDialog && selectedMaterial != null) {
        LaundryServiceSelectionDialog(
            material = selectedMaterial!!,
            onDismiss = {
                showServiceDialog = false
                selectedMaterial = null
            },
            onAddToCart = { serviceName, price, quantity ->
                val cartItem = LaundryCartItem(
                    materialId = selectedMaterial!!.id,
                    materialName = selectedMaterial!!.name,
                    serviceType = serviceName,
                    quantity = quantity,
                    unitPrice = price
                )
                posViewModel.addItemToCart(cartItem)
                
                showServiceDialog = false
                selectedMaterial = null
            }
        )
    }

    if (posState.showCartSheet) {
        LaundryCartBottomSheet(
            state = posState,
            onDismiss = { posViewModel.toggleCartSheet(false) },
            onUpdateQuantity = { item, newQty -> posViewModel.updateItemQuantity(item, newQty) },
            onCheckout = { paymentMethod -> posViewModel.checkout(paymentMethod) }
        )
    }

    if (posState.showCheckoutSuccess) {
        AlertDialog(
            onDismissRequest = { posViewModel.dismissSuccessDialog() },
            title = { CustomText("Success") },
            text = { CustomText("Laundry transaction completed successfully!") },
            confirmButton = {
                TextButton(onClick = { posViewModel.dismissSuccessDialog() }) {
                    CustomText("OK")
                }
            }
        )
    }
}

@Composable
fun LaundryMaterialCard(material: LaundryMaterial, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                CustomText(material.name, fontWeight = FontWeight.Bold)
                CustomText("Select to choose service", style = MaterialTheme.typography.bodySmall.fontSize)
            }
            Icon(Icons.Default.AddShoppingCart, contentDescription = "Select")
        }
    }
}

@Composable
fun LaundryServiceSelectionDialog(
    material: LaundryMaterial,
    onDismiss: () -> Unit,
    onAddToCart: (String, Double, Int) -> Unit
) {
    var quantity by remember { mutableStateOf(1) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { CustomText("Select Service: ${material.name}") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    CustomText("Quantity:")
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = { if (quantity > 1) quantity-- }) {
                            CustomText("-", fontWeight = FontWeight.Bold)
                        }
                        CustomText(quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                        IconButton(onClick = { quantity++ }) {
                            CustomText("+", fontWeight = FontWeight.Bold)
                        }
                    }
                }
                
                Divider()

                ServiceOption("Washing Only", material.washingPrice) { price ->
                    onAddToCart("Washing Only", price, quantity)
                }
                ServiceOption("Ironing Only", material.ironingPrice) { price ->
                    onAddToCart("Ironing Only", price, quantity)
                }
                ServiceOption("Wash & Iron", material.washAndIronPrice) { price ->
                    onAddToCart("Wash & Iron", price, quantity)
                }
            }
        },
        confirmButton = {},
        dismissButton = {
            TextButton(onClick = onDismiss) {
                CustomText("Cancel")
            }
        }
    )
}

@Composable
fun ServiceOption(name: String, price: Double, onClick: (Double) -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick(price) }
            .padding(vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        CustomText(name)
        CustomText("${Util.CURRENCY_CODE}$price", fontWeight = FontWeight.Bold)
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LaundryCartBottomSheet(
    state: LaundryPOSState,
    onDismiss: () -> Unit,
    onUpdateQuantity: (LaundryCartItem, Int) -> Unit,
    onCheckout: (String) -> Unit
) {
    ModalBottomSheet(onDismissRequest = onDismiss) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            CustomText("Laundry Cart", fontWeight = FontWeight.Bold, modifier = Modifier.padding(bottom = 16.dp))
            
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
                                CustomText("${item.materialName} - ${item.serviceType}", fontWeight = FontWeight.Bold)
                                CustomText("${Util.CURRENCY_CODE}${item.unitPrice} x ${item.quantity}")
                            }
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                IconButton(onClick = { onUpdateQuantity(item, item.quantity - 1) }) {
                                    CustomText("-", fontWeight = FontWeight.Bold)
                                }
                                CustomText(item.quantity.toString(), modifier = Modifier.padding(horizontal = 8.dp))
                                IconButton(onClick = { onUpdateQuantity(item, item.quantity + 1) }) {
                                    CustomText("+", fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                        Divider()
                    }
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
                    Row(horizontalArrangement = Arrangement.spacedBy(16.dp), modifier = Modifier.fillMaxWidth()) {
                        Button(
                            onClick = { onCheckout("Cash") },
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomText("Pay Cash")
                        }
                        Button(
                            onClick = { onCheckout("POS") },
                            modifier = Modifier.weight(1f)
                        ) {
                            CustomText("Pay POS")
                        }
                    }
                }
            }
        }
    }
}
