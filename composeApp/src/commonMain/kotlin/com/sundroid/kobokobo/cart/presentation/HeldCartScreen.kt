package com.sundroid.kobokobo.cart.presentation

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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.cart.data.database.HeldCartEntity
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.dashboard.utils.Util
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HeldCartsScreen(
    viewModel: DashboardViewModel,
    onNavigateBack: () -> Unit
) {
    val state by viewModel.state.collectAsStateWithLifecycle()
    val heldCarts = state.heldCarts

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Held Carts") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        if (heldCarts.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                Text("No held carts.")
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues),
                contentPadding = PaddingValues(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(heldCarts, key = { it.reference }) { heldCart ->
                    HeldCartItemCard(
                        heldCart = heldCart,
                        onResume = {
                            viewModel.resumeHeldCart(heldCart)
                            onNavigateBack() // Go back to POS after resuming
                        },
                        onDelete = { viewModel.deleteHeldCart(heldCart) }
                    )
                }
            }
        }
    }
}

@Composable
fun HeldCartItemCard(
    heldCart: HeldCartEntity,
    onResume: () -> Unit,
    onDelete: () -> Unit
) {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())

    Card(modifier = Modifier.fillMaxWidth()) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Ref: ${heldCart.reference.take(8)}...",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Util.currencyFormat(heldCart.totalAmount),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary
                )
            }
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = dateFormat.format(Date(heldCart.timestamp)),
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "${heldCart.items.size} items",
                style = MaterialTheme.typography.bodyMedium
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End,
                verticalAlignment = Alignment.CenterVertically
            ) {
                TextButton(onClick = onDelete) {
                    Icon(Icons.Default.Delete, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Delete", color = MaterialTheme.colorScheme.error)
                }
                Spacer(Modifier.width(8.dp))
                Button(onClick = onResume) {
                    Icon(Icons.Default.PlayArrow, contentDescription = null)
                    Spacer(Modifier.width(4.dp))
                    Text("Resume")
                }
            }
        }
    }
}