package com.sundroid.kobokobo.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AddShoppingCart
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.dashboard.domain.Category
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.dashboard.utils.Util.currencyFormat
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import com.sundroid.kobokobo.login.presentation.UserViewModel


@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun POSSCreenBody(
    viewModel: DashboardViewModel,
    userViewModel: UserViewModel,
    modifier: Modifier = Modifier,
    isMobile: Boolean,
    onShowCart: () -> Unit
) {

    val state by viewModel.state.collectAsStateWithLifecycle()
    val userState by userViewModel.state.collectAsStateWithLifecycle()
    val selectedCategory = remember { mutableStateOf<Category?>(null) } // Handle "All" as null or specific ID logic
    var searchValue by remember { mutableStateOf("") }

    Box(modifier = modifier.fillMaxSize().background(MaterialTheme.colorScheme.background)) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // --- TOP BAR AREA: Search & Title ---
            Column(modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = searchValue,
                        onValueChange = {
                            searchValue = it
                            viewModel.searchItems(it)
                        },
                        modifier = Modifier.weight(1f),
                        placeholder = { Text("Search products...") },
                        maxLines = 1,
                        singleLine = true,
                        leadingIcon = {
                            Icon(Icons.Default.Search, contentDescription = null)
                        },
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = MaterialTheme.colorScheme.surface,
                            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                        )
                    )

                    if (userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id)) {
                        IconButton(
                            onClick = { NavigationViewModel.businessNavController?.navigate(Route.AddProductRoute) },
                            modifier = Modifier.size(56.dp).clip(RoundedCornerShape(12.dp)),
                            colors = IconButtonDefaults.filledIconButtonColors(
                                containerColor = MaterialTheme.colorScheme.primary,
                                contentColor = MaterialTheme.colorScheme.onPrimary
                            )
                        ) {
                            Icon(Icons.Default.Add, contentDescription = "Add Product")
                        }
                    }
                }
            }

            // --- CATEGORIES ROW ---
            if (state.categories.isNotEmpty()) {
                LazyRow(
                    contentPadding = PaddingValues(horizontal = 16.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    items(state.categories) { category ->
                        FilterChip(
                            selected = selectedCategory.value == category,
                            onClick = {
                                selectedCategory.value = category
                                viewModel.selectCategory(category.categoryId)
                            },
                            label = { Text(category.categoryName) },
                             colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primary,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                            )
                        )
                    }
                }
            }

            // --- PRODUCT GRID ---
            if (state.visibleProducts.isNotEmpty() || state.allProducts.isNotEmpty()) {
                // Use visibleProducts if searching/filtering, otherwise allProducts?
                // Logic depends on VM. Assuming visibleProducts is the source of truth for display
                LazyVerticalGrid(
                    columns = GridCells.Adaptive(minSize = 120.dp),
                    contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 100.dp), // Bottom padding for FAB
                    horizontalArrangement = Arrangement.spacedBy(12.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    items(state.visibleProducts.sortedByDescending { it.sortOrder }, key = { it.id }) { product ->
                         AnimatedVisibility(
                            visible = true,
                            enter = fadeIn(animationSpec = tween(300)) + slideInVertically { it / 4 },
                             modifier = Modifier
                        ) {
                            ProductCard(
                                viewModel = viewModel, 
                                product = product,
                                cartItems = state.cartItems,
                                hasAddInventoryPermission = userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id)
                            )
                        }
                    }
                }
            } else {
                 Box(modifier = Modifier.fillMaxSize().weight(1f), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                         Icon(Icons.Default.Search, contentDescription = null, modifier = Modifier.size(48.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                         Spacer(modifier = Modifier.height(8.dp))
                        Text("No products found", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        // --- DASHBOARD FABs ---
        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp),
            horizontalAlignment = Alignment.End
        ) {
             if (isMobile) {
                FloatingActionButton(
                    onClick = { viewModel.holdCurrentCart() },
                    containerColor = MaterialTheme.colorScheme.secondaryContainer,
                    contentColor = MaterialTheme.colorScheme.onSecondaryContainer
                ) {
                     Icon(Icons.Default.AddShoppingCart, "Hold Cart")
                }

                BadgedBox(
                    badge = {
                        val itemCount = state.cartItems.sumOf { it.quantity }
                        if (itemCount > 0) {
                            Badge { Text("$itemCount") }
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = onShowCart,
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = MaterialTheme.colorScheme.onPrimary
                    ) {
                        Icon(Icons.Default.ShoppingCart, "View Cart")
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    viewModel: DashboardViewModel, 
    product: Product,
    cartItems: List<com.sundroid.kobokobo.cart.domain.CartItem> = emptyList(),
    hasAddInventoryPermission: Boolean = false
) {
    ElevatedCard(
        modifier = Modifier
            .fillMaxWidth()
            .height(124.dp)
            .clickable { viewModel.addItemToCart(product) },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.elevatedCardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.elevatedCardColors(containerColor = MaterialTheme.colorScheme.surface)
    ) {
        Column(
            modifier = Modifier.padding(12.dp).fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Placeholder for Image removed to save space as per requirement


            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                        maxLines = 2,
                        overflow = TextOverflow.Ellipsis,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    val cartQuantity = cartItems.find { it.itemId == product.id }?.quantity ?: 0
                    val remainingStock = (product.stock ?: 0.0) - cartQuantity
                    Text(
                        text = "Stock: ${if (remainingStock.rem(1) == 0.0) remainingStock.toInt() else remainingStock}",
                        style = MaterialTheme.typography.bodySmall,
                        color = if (remainingStock > 0) MaterialTheme.colorScheme.outline else MaterialTheme.colorScheme.error
                    )
                }

                    Box {
                        var expanded by remember { mutableStateOf(false) }
                        IconButton(onClick = { expanded = true }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.Default.MoreVert, contentDescription = "More options")
                        }
                        DropdownMenu(
                            expanded = expanded,
                            onDismissRequest = { expanded = false }
                        ) {
                            DropdownMenuItem(
                                text = { Text("Details") },
                                onClick = {
                                    expanded = false
                                    NavigationViewModel.businessNavController?.navigate(Route.ProductDetailRoute(product.id))
                                }
                            )
                        }
                    }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = currencyFormat(product.unitPrice),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.primary
                )
                
                IconButton(
                    onClick = { viewModel.addItemToCart(product) },
                    colors = IconButtonDefaults.filledIconButtonColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(Icons.Default.Add, contentDescription = "Add", tint = MaterialTheme.colorScheme.onPrimaryContainer, modifier = Modifier.size(16.dp))
                }
            }
        }
    }
}