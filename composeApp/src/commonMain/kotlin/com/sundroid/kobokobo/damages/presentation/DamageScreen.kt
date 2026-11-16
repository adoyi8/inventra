package com.sundroid.kobokobo.damages.presentation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.audit.presentation.ScreenSize
import com.sundroid.kobokobo.damages.data.domain.Damage
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.transactionHistory.presentation.formatDate
import kotlinx.coroutines.launch
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@Suppress("UnusedBoxWithConstraintsScope")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamagesScreen(
    onNavigateBack: () -> Unit,
    businessId: String,
    staffId: String,
    staffName: String,
    viewModel: DamagesViewModel = koinViewModel(parameters = { parametersOf(businessId, staffId, staffName) })
) {
    val state by viewModel.state.collectAsStateWithLifecycle()

    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    // Handle submission success/error for the dialog
    LaunchedEffect(state.isSubmitSuccess) {
        if (state.isSubmitSuccess) {
            viewModel.hideAddDialog()
            scope.launch { snackbarHostState.showSnackbar("Damage recorded successfully") }
            viewModel.resetSubmitSuccess()
        }
    }
    state.submitErrorMessage?.let { message ->
        LaunchedEffect(message) {
            scope.launch { snackbarHostState.showSnackbar(message) }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Damages") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        },
        floatingActionButton = {
            FloatingActionButton(onClick = viewModel::showAddDialog) {
                Icon(Icons.Default.Add, contentDescription = "Add Damage")
            }
        },
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            val screenSize = if (maxWidth < 600.dp) ScreenSize.COMPACT else ScreenSize.EXPANDED

            if (state.isLoadingList && state.damages.isEmpty()) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            } else if (state.listErrorMessage != null) {
                Text(
                    text = state.listErrorMessage!!,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.align(Alignment.Center)
                )
            } else if (state.damages.isEmpty()) {
                Text("No damages recorded.", modifier = Modifier.align(Alignment.Center))
            } else {
                // Re-use a similar list structure to Expenses
                DamagesList(
                    screenSize = screenSize,
                    damages = state.damages,
                    modifier = Modifier.fillMaxSize()
                )
            }

            if (state.showAddDialog) {
                AddDamageDialog(
                    state = state,
                    onDismiss = viewModel::hideAddDialog,
                    onSearchQueryChanged = viewModel::onSearchQueryChanged,
                    onProductSelected = viewModel::selectProduct,
                    onQuantityChanged = viewModel::onQuantityChanged,
                    onReasonChanged = viewModel::onReasonChanged,
                    onSubmit = viewModel::submitDamage
                )
            }

            if (state.isSubmitting) {
                CircularProgressIndicator()
            }
        }
    }
}

@Composable
fun DamagesList(
    screenSize: ScreenSize,
    damages: List<Damage>,
    modifier: Modifier = Modifier
) {
    // Simplified for brevity, you can add a table view for expanded screens like in ExpenseList
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(damages) { damage ->
            DamageCardItem(damage = damage)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DamageCardItem(damage: Damage) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = damage.productName,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = Util.currencyFormat(damage.totalCost),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.error // Red for loss
                )
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("Qty: ${damage.quantity}", style = MaterialTheme.typography.bodyMedium)
                Text("Cost/Unit: ${Util.currencyFormat(damage.costPrice)}", style = MaterialTheme.typography.bodyMedium)
            }
            if (damage.reason.isNotBlank()) {
                Text("Reason: ${damage.reason}", style = MaterialTheme.typography.bodyMedium)
            }
            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                Text("By: ${damage.staffName}", style = MaterialTheme.typography.bodySmall)
                Text(formatDate(damage.createdAt), style = MaterialTheme.typography.bodySmall)
            }
        }
    }
}