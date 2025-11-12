package com.ikemba.inventrar.business.presentation.businessList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.business.AnimatedButton
import com.ikemba.inventrar.business.RoomItem
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.core.presentation.components.CustomButton
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.login.presentation.UserViewModel
import kotlinx.coroutines.delay

// Assuming you get this from your Koin graph
// val viewModel: BusinessListViewModel = koinInject()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(viewModel: BusinessListViewModel, userViewModel: UserViewModel) {
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    

    Scaffold(
        floatingActionButton = {
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Business") },
                text = { Text("Create Business") }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            when {
                uiState.isLoading -> {
                    CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
                }
                uiState.error != null -> {
                    Text(
                        text = uiState.error!!,
                        color = MaterialTheme.colorScheme.error,
                        modifier = Modifier.align(Alignment.Center)
                    )
                }
                uiState.businesses.isEmpty() -> {
                    Column(
                        modifier = Modifier.align(Alignment.Center).padding(24.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "You are not part of any business yet.",
                            style = MaterialTheme.typography.titleMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(Modifier.height(8.dp))
                        Text(
                            text = "Create one to get started.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                        Button(onClick = { showCreateDialog = true }) { Text("Create Business") }
                    }
                }
                else -> {
                    LazyColumn(
                        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp, vertical = 8.dp),
                        verticalArrangement = Arrangement.spacedBy(12.dp),
                        contentPadding = PaddingValues(bottom = 88.dp)
                    ) {
                        itemsIndexed(
                            uiState.businesses,
                            key = { _, room -> room.businessId }) { index, room ->
                            var isItemVisible by remember { mutableStateOf(false) }
                            LaunchedEffect(Unit) {
                                delay(100L * index) // Staggered delay
                                isItemVisible = true
                            }
                            AnimatedVisibility(
                                visible = isItemVisible,
                                enter = fadeIn(animationSpec = tween(300)) + slideInHorizontally(
                                    initialOffsetX = { it / 2 }),
                                exit = fadeOut(animationSpec = tween(300))
                            ) {
                                val onClick ={
                                    userViewModel.setSelectedBusiness(room)
                                }
                                BusinessItem(room, onClick)
                            }
                        }
                    }


                }
            }

            if (showCreateDialog) {
                CreateBusinessDialog(
                    onDismiss = { showCreateDialog = false },
                    onCreate = { businessName ->
                        viewModel.createBusiness(businessName)
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}

@Composable
fun CreateBusinessDialog(onDismiss: () -> Unit, onCreate: (String) -> Unit) {
    var businessName by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Business") },
        text = {
            Column(modifier = Modifier.fillMaxWidth()) {
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(businessName) }, enabled = businessName.isNotBlank()) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun BusinessItem(room: Business, onClick: () -> Unit) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            CustomText(
                room.name,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            CustomButton(
                onClick = onClick,
                text = "Walk In"
            )
        }
    }
}