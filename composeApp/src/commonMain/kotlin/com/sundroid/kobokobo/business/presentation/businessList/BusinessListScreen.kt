package com.sundroid.kobokobo.business.presentation.businessList

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.foundation.BorderStroke
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.google.firebase.auth.FirebaseAuth
import com.sundroid.kobokobo.business.data.dto.BusinessWithStatus
import com.sundroid.kobokobo.business.presentation.BusinessListViewModel
import com.sundroid.kobokobo.core.presentation.components.CustomButton
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.dashboard.utils.Util
import com.sundroid.kobokobo.login.presentation.UserViewModel
import kotlinx.coroutines.delay

// Assuming you get this from your Koin graph
// val viewModel: BusinessListViewModel = koinInject()

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessListScreen(viewModel: BusinessListViewModel, userViewModel: UserViewModel)
{
    val uiState by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    val userState by userViewModel.state.collectAsStateWithLifecycle()
    val userId = FirebaseAuth.getInstance().currentUser?.uid
    LaunchedEffect(FirebaseAuth.getInstance().currentUser){
        if (userId != null) {
            println("Starting real-time listener for user: $userId")
            viewModel.startListeningForBusinesses(userId)
        }
    }

    Scaffold(
        floatingActionButton = {
            if(FirebaseAuth.getInstance().currentUser?.email == Util.DEFAULT_ADMIN_EMAIL){
            ExtendedFloatingActionButton(
                onClick = { showCreateDialog = true },
                icon = { Icon(Icons.Default.Add, contentDescription = "Create Business") },
                text = { Text("Create Business") }
            )
        }}
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
                            text = "Refer to your Manager/Admin to add you.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        Spacer(Modifier.height(16.dp))
                     //   Button(onClick = { showCreateDialog = true }) { Text("Create Business") }
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
                            key = { _, room -> room.business.businessId}) { index, room ->
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
                                    if (room.isActive) {
                                        userViewModel.setSelectedBusiness(room.business)
                                    }
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
                    onCreate = { businessName, address, features ->
                        viewModel.createBusiness(businessName, address, features)
                        showCreateDialog = false
                    }
                )
            }
        }
    }
}



@Composable
fun CreateBusinessDialog(onDismiss: () -> Unit, onCreate: (String, String, List<String>) -> Unit) {
    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }
    val allFeatures = listOf("Sales", "Hotels & Lodgings", "Laundry", "Services")
    var selectedFeatures by remember { mutableStateOf(emptySet<String>()) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create New Business") },
        text = {
            Column(modifier = Modifier.fillMaxWidth().verticalScroll(rememberScrollState())) {
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Text("Select Features:", fontWeight = FontWeight.SemiBold)
                allFeatures.forEach { feature ->
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Checkbox(
                            checked = selectedFeatures.contains(feature),
                            onCheckedChange = { checked ->
                                selectedFeatures = if (checked) {
                                    selectedFeatures + feature
                                } else {
                                    selectedFeatures - feature
                                }
                            }
                        )
                        Text(text = feature)
                    }
                }
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(businessName, address, selectedFeatures.toList()) }, 
                enabled = businessName.isNotBlank()
            ) { Text("Create") }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
@Composable
fun BusinessItem(item: BusinessWithStatus, onClick: () -> Unit) {
    val business = item.business
    // Determine if the item should appear disabled
    val isEnabled = item.isActive

    // Optional: Change card colors based on enabled state
    val cardColors = if (isEnabled) {
        CardDefaults.cardColors()
    } else {
        CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
            contentColor = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
        )
    }

    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = cardColors,
        // Optional: Add a border for inactive items to make them distinct
        border = if (!isEnabled) BorderStroke(1.dp, MaterialTheme.colorScheme.outline.copy(alpha = 0.3f)) else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Column {
                CustomText(
                    business.name,
                    fontWeight = FontWeight.SemiBold,
                    // Color will be automatically adjusted by cardColors contentColor
                )
                // Optional: Show why it's disabled
                if (!isEnabled) {
                    val statusText = when {
                        !item.isBusinessActive -> "(Business Inactive)"
                        !item.isStaffActive -> "(Your Access Suspended)"
                        else -> ""
                    }
                    Text(
                        text = statusText,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }

            // Pass the enabled state to your CustomButton
            CustomButton(
                onClick = onClick,
                text = "Select",
                enabled = isEnabled // Assuming CustomButton has an 'enabled' parameter
            )
        }
    }
}