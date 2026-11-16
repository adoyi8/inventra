package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.inventory.data.domain.model.Location
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LocationListScreen(
    viewModel: LocationListViewModel = koinViewModel(),
    onNavigateToAddLocation: () -> Unit,
    onNavigateToLocationDetail: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Locations") }
            )
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddLocation
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Location")
            }
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }
            
            uiState.errorMessage?.let { 
                Text(
                    text = it, 
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.locations) { location ->
                    LocationCard(
                        location = location,
                        onClick = { onNavigateToLocationDetail(location.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun LocationCard(
    location: Location,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = location.name,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "Type: ${location.locationType.name}",
                style = MaterialTheme.typography.bodyMedium
            )
            if (location.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = location.description,
                    style = MaterialTheme.typography.bodyMedium
                )
            }
        }
    }
}
