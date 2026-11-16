package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.dashboard.utils.Util.currencyFormat

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductDetailScreen(
    viewModel: ProductFormViewModel,
    onNavigateBack: () -> Unit,
    onNavigateToEditProduct: () -> Unit,
    hasEditPermission: Boolean,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(uiState.name) {
        if (uiState.name.isNotBlank()) {
            title.value = uiState.name
        } else {
            title.value = "Product Details"
        }
    }

    Box(
        modifier = Modifier.fillMaxSize().padding(16.dp),
        contentAlignment = Alignment.TopCenter
    ) {
        if (uiState.isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
        } else if (uiState.errorMessage != null && !uiState.isInitialLoad) {
            Text(uiState.errorMessage ?: "An error occurred", color = MaterialTheme.colorScheme.error, modifier = Modifier.align(Alignment.Center))
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(24.dp)
            ) {
                // Header Actions
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }

                    if (hasEditPermission) {
                        Button(
                            onClick = onNavigateToEditProduct,
                            colors = ButtonDefaults.buttonColors(
                                containerColor = MaterialTheme.colorScheme.primary
                            ),
                            shape = RoundedCornerShape(12.dp)
                        ) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(8.dp))
                            Text("Update Product")
                        }
                    }
                }

                // Header section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = uiState.name,
                            style = MaterialTheme.typography.headlineMedium.copy(fontWeight = FontWeight.Bold),
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = "SKU: ${uiState.sku}",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onPrimaryContainer.copy(alpha = 0.8f)
                        )
                    }
                }

                // Details section
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Pricing Information",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider()
                        
                        DetailRow(label = "Selling Price", value = currencyFormat(uiState.unitPrice.toDoubleOrNull() ?: 0.0), isHighlight = true)
                        DetailRow(label = "Cost Price", value = currencyFormat(uiState.costPrice.toDoubleOrNull() ?: 0.0))
                        DetailRow(label = "Discount", value = "${uiState.discount}%")
                        DetailRow(label = "VAT", value = "${uiState.vat}%")
                    }
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    shape = RoundedCornerShape(16.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Inventory",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                            color = MaterialTheme.colorScheme.primary
                        )
                        HorizontalDivider()
                        
                        val categoryName = uiState.categories.find { it.id == uiState.selectedCategoryId }?.name ?: "None"
                        DetailRow(label = "Category", value = categoryName)
                        DetailRow(label = "Current Stock", value = uiState.stock)
                    }
                }

                if (uiState.description.isNotBlank()) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(16.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(24.dp),
                            verticalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            Text(
                                text = "Description",
                                style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.SemiBold),
                                color = MaterialTheme.colorScheme.primary
                            )
                            HorizontalDivider()
                            Text(
                                text = uiState.description,
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String, isHighlight: Boolean = false) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Text(
            text = value,
            style = if (isHighlight) MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold) else MaterialTheme.typography.bodyLarge.copy(fontWeight = FontWeight.Medium),
            color = if (isHighlight) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}
