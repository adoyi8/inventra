package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sundroid.kobokobo.audit.presentation.AuditTrailList
import com.sundroid.kobokobo.audit.presentation.AuditTrailViewModel
import com.sundroid.kobokobo.audit.presentation.ScreenSize
import org.koin.compose.viewmodel.koinViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductAuditTrailScreen(
    productId: String,
    onNavigateBack: () -> Unit,
    viewModel: AuditTrailViewModel = koinViewModel()
) {
    LaunchedEffect(productId) {
        viewModel.setProductId(productId)
    }

    val state = viewModel.state.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Product History") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
             AuditTrailList(
                screenSize = ScreenSize.COMPACT, // defaulting to compact for now, or pass from parent
                auditTrails = state.value.auditTrails,
                staffList = state.value.staffList,
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}
