package com.sundroid.kobokobo.cart.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ElevatedButton
import androidx.compose.material3.FilledIconButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.cart.domain.CartItem
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.dashboard.utils.Util.currencyFormat
import com.sundroid.kobokobo.login.presentation.UserViewModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.remove
import org.jetbrains.compose.resources.painterResource

@Composable
fun CartScreen(viewModel: DashboardViewModel,userViewModel: UserViewModel, modifier: Modifier = Modifier) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.state.collectAsState()
    // Split screen into content (list) and footer (summary)
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.surface),
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        
        // --- HEADER ---
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Current Order",
                    style = MaterialTheme.typography.headlineSmall,
                     fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface
                )
                Text(
                    text = "#${state.reference}",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
             FilledIconButton(
                 onClick = { viewModel.clearCart() },
                 colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.errorContainer)
            ) {
                 Icon(Icons.Default.Delete, contentDescription = "Clear", tint = MaterialTheme.colorScheme.onErrorContainer)
             }
        }

        // --- CART ITEMS LIST ---
        if (state.cartItems.isEmpty()) {
            Box(modifier = Modifier.weight(1f).fillMaxWidth(), contentAlignment = Alignment.Center) {
                Text("Cart is empty", color = MaterialTheme.colorScheme.outline)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(state.cartItems, key = { it.itemId ?: "" }) { item ->
                    AnimatedVisibility(
                        visible = true,
                        enter = slideInHorizontally() + fadeIn(),
                        exit = slideOutHorizontally() + fadeOut()
                    ) {
                        CartItemCard(item, viewModel)
                    }
                }
            }
        }

        // --- SUMMARY & ACTIONS FOOTER ---
        Surface(
             modifier = Modifier.fillMaxWidth(),
             shadowElevation = 16.dp,
             tonalElevation = 2.dp,
             shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
        ) {
            Column(modifier = Modifier.padding(24.dp)) {
                
                // Payment Method Selector
                // Payment Method Selector
                Row(
                    modifier = Modifier.fillMaxWidth().padding(bottom = 12.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Payment Method", 
                        style = MaterialTheme.typography.titleSmall, 
                        fontWeight = FontWeight.Bold
                    )
                    

                    if (userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id)) {
                        Text(
                            text = "Manage",
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.clickable {
                                NavigationViewModel.businessNavController?.navigate(Route.PaymentMethodsRoute)
                            }
                        )
                    }
                }
                
                val scrollState = androidx.compose.foundation.rememberScrollState()
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp)
                        .horizontalScroll(scrollState),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displayedMethods = state.paymentMethods.ifEmpty {
                        listOf(
                            com.sundroid.kobokobo.business.data.domain.model.PaymentMethod("cash", "Cash"),
                        )
                    }

                    displayedMethods.forEach { method ->
                        val isSelected = state.paymentMethod.equals(method.id, ignoreCase = true) || state.paymentMethod.equals(method.name, ignoreCase = true)
                        
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable {
                                viewModel.setPaymentMethod(method.id.ifEmpty { method.name })
                            }
                        ) {
                            androidx.compose.material3.RadioButton(
                                selected = isSelected,
                                onClick = {
                                    viewModel.setPaymentMethod(method.id.ifEmpty { method.name })
                                }
                            )
                            Text(
                                text = method.name,
                                style = MaterialTheme.typography.bodyMedium,
                                color = MaterialTheme.colorScheme.onSurface,
                                modifier = Modifier.padding(end = 8.dp)
                            )
                        }
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 16.dp)
                        .clickable { viewModel.setProceedWithoutPayment(!state.proceedWithoutPayment) },
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    androidx.compose.material3.Checkbox(
                        checked = state.proceedWithoutPayment,
                        onCheckedChange = viewModel::setProceedWithoutPayment
                    )
                    Text(
                        text = "Proceed without payment",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }

                Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                Spacer(modifier = Modifier.height(16.dp))

                // Totals
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                     Text("Total Amount", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                     
                     val total = state.cartItems.sumByDouble { ((it.price * (it.vatRate ?: 0.0) * 0.01) * it.quantity) + (it.price * it.quantity) }
                     Text(
                         text = currencyFormat(total), 
                         style = MaterialTheme.typography.headlineMedium, 
                         fontWeight = FontWeight.ExtraBold,
                         color = MaterialTheme.colorScheme.primary
                     )
                }
                
                Spacer(modifier = Modifier.height(24.dp))

                // Checkout Button
                ElevatedButton(
                    onClick = { viewModel.toggleShowConfirmTransaction(true) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.elevatedButtonColors(
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text("Checkout", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

@Composable
fun CartItemCard(item: CartItem, viewModel: DashboardViewModel) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {

            
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = item.itemName ?: "Unknown Item", 
                    style = MaterialTheme.typography.titleMedium, 
                    fontWeight = FontWeight.SemiBold,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = currencyFormat(item.price), 
                    style = MaterialTheme.typography.bodyMedium, 
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            // Quantity Controls
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.background(MaterialTheme.colorScheme.surface, RoundedCornerShape(8.dp)).padding(4.dp)
            ) {
                 IconButton(
                     onClick = { viewModel.subtractItemFromCart(item) },
                     modifier = Modifier.size(28.dp)
                 ) {
                     Icon(painterResource(Res.drawable.remove), contentDescription = "Decrease", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(12.dp))
                 }
                
                 Text(
                     text = item.quantity.toString(), 
                     style = MaterialTheme.typography.titleSmall,
                     fontWeight = FontWeight.Bold,
                     modifier = Modifier.width(20.dp),
                     textAlign = TextAlign.Center
                 )

                 IconButton(
                     onClick = { viewModel.addItemToCart(item) },
                     modifier = Modifier.size(28.dp)
                 ) {
                     Icon(Icons.Default.Add, contentDescription = "Increase", tint = MaterialTheme.colorScheme.primary, modifier = Modifier.size(16.dp))
                 }
            }
            
            Spacer(modifier = Modifier.width(8.dp))
            
            IconButton(
                onClick = { viewModel.removeItemFromCart(item) },
                colors = IconButtonDefaults.iconButtonColors(contentColor = MaterialTheme.colorScheme.error)
            ) {
                Icon(Icons.Default.Delete, contentDescription = "Remove")
            }
        }
    }
}
