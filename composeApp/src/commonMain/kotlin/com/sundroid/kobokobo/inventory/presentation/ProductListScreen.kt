package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AssistChip
import androidx.compose.material3.AssistChipDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.inventory.data.domain.model.Product
import kotlinx.coroutines.delay
import org.koin.compose.viewmodel.koinViewModel

@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductListScreen(
    viewModel: ProductListViewModel = koinViewModel(),
    onNavigateToAddProduct: () -> Unit,
    onNavigateToProductDetail: (String) -> Unit,
    onNavigateToProductHistory: (String) -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()
    var searchQuery by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        title.value = "Product Catalog"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddProduct,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Product")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            // Search Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp),
                placeholder = { Text("Search products by name or SKU...") },
                leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                shape = RoundedCornerShape(12.dp),
                singleLine = true,
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Content
            BoxWithConstraints(modifier = Modifier.fillMaxSize())
            {

                val width = maxWidth
                val columns = when {
                    width < 600.dp -> 1
                    width < 1000.dp -> 2 // Two columns for tablets
                    else -> 3 // 3 for desktop
                }

                if (uiState.isLoading) {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                } else {
                    uiState.errorMessage?.let {
                        Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(8.dp))
                    }

                    // Local Filter logic for responsiveness
                    val filteredProducts = remember(uiState.products, searchQuery) {
                        if (searchQuery.isBlank()) uiState.products
                        else uiState.products.filter {
                            it.name.contains(searchQuery, ignoreCase = true) ||
                            it.sku.contains(searchQuery, ignoreCase = true)
                        }
                    }

                    if (filteredProducts.isEmpty() && !uiState.isLoading) {
                         Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                             Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                 Icon(Icons.Default.Inventory2, contentDescription = null, modifier = Modifier.size(64.dp), tint = MaterialTheme.colorScheme.outline)
                                 Spacer(modifier = Modifier.height(16.dp))
                                 Text("No products found", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                             }
                         }
                    } else {
                        LazyVerticalGrid(
                            columns = GridCells.Fixed(columns),
                            verticalArrangement = Arrangement.spacedBy(16.dp),
                            horizontalArrangement = Arrangement.spacedBy(16.dp),
                            contentPadding = PaddingValues(bottom = 72.dp)
                        ) {
                            itemsIndexed(filteredProducts) { index, product ->
                                ProductCard(
                                    product = product,
                                    index = index,
                                    onClick = { onNavigateToProductDetail(product.id) },
                                    onHistoryClick = { onNavigateToProductHistory(product.id) }
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ProductCard(
    product: Product,
    index: Int,
    onClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 30L)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 4 } + scaleIn(initialScale = 0.95f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(12.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Product Icon/Image Placeholder
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.3f),
                    modifier = Modifier.size(64.dp)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.ShoppingBag, 
                            contentDescription = null, 
                            tint = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.width(16.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = product.name,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "SKU: ${product.sku}",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(4.dp))
//                    Text(
//                        text = "Stock: ${product.stock?.toInt() ?: 0}",
//                        style = MaterialTheme.typography.bodySmall,
//                        color = MaterialTheme.colorScheme.onSurfaceVariant
//                    )
                     Spacer(modifier = Modifier.height(8.dp))
                     Row(verticalAlignment = Alignment.CenterVertically) {
                         Text(
                             text = "$${String.format("%.2f", product.unitPrice)}",
                             style = MaterialTheme.typography.titleSmall,
                             fontWeight = FontWeight.Bold,
                             color = MaterialTheme.colorScheme.primary
                         )
                         if (product.discount != null && product.discount > 0) {
                             Spacer(modifier = Modifier.width(8.dp))
                             AssistChip(
                                 onClick = {},
                                 label = { Text("-${product.discount}%", style = MaterialTheme.typography.labelSmall) },
                                 colors = AssistChipDefaults.assistChipColors(containerColor = MaterialTheme.colorScheme.errorContainer, labelColor = MaterialTheme.colorScheme.onErrorContainer),
                                 modifier = Modifier.height(20.dp)
                             )
                         }
                     }
                }

                IconButton(onClick = onHistoryClick) {
                    Icon(
                        imageVector = Icons.Default.History,
                        contentDescription = "History",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
            }
        }
    }
}
