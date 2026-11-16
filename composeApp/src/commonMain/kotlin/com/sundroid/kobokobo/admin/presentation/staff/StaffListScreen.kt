package com.sundroid.kobokobo.admin.presentation.staff

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.scaleIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Circle
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.core.presentation.components.CustomText
import org.koin.compose.viewmodel.koinViewModel
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StaffListScreen(
    onNavigateToAddStaff: () -> Unit,
    onNavigateToDetails: (String) -> Unit,
    viewModel: StaffListViewModel = koinViewModel(),
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()
    
    LaunchedEffect(Unit){
        title.value = "Staff Management"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddStaff,
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Staff")
            }
        }
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val width = maxWidth
            val columns = when {
                width < 600.dp -> 1
                width < 1000.dp -> 2
                else -> 3
            }

            if (uiState.isLoading) {
                LinearProgressIndicator(modifier = Modifier.fillMaxWidth())
            }

            uiState.errorMessage?.let { 
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CustomText(it, color = MaterialTheme.colorScheme.error) 
                }
            }

            LazyVerticalGrid(
                columns = GridCells.Fixed(columns),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                contentPadding = PaddingValues(bottom = 72.dp) // Space for FAB
            ) {
                itemsIndexed(uiState.staff) { index, staff ->
                    StaffCard(
                        staff = staff, 
                        index = index,
                        onClick = { onNavigateToDetails(staff.staffId) }
                    )
                }
            }
        }
    }
}

@Composable
fun StaffCard(staff: Staff, index: Int, onClick: () -> Unit) {
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L)
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn() + slideInVertically { it / 4 } + scaleIn(initialScale = 0.9f)
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(onClick = onClick),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier.padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Avatar
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.secondaryContainer),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = staff.name.take(1).uppercase(),
                        style = MaterialTheme.typography.headlineSmall,
                        color = MaterialTheme.colorScheme.onSecondaryContainer
                    )
                }
                
                Spacer(modifier = Modifier.width(16.dp))
                
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = staff.name, 
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        maxLines = 1
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = staff.roleName, 
                        style = MaterialTheme.typography.bodyMedium, 
                        color = MaterialTheme.colorScheme.secondary
                    )
                }

                // Status Indicator
                Column(horizontalAlignment = Alignment.End) {
                   Icon(
                       imageVector = Icons.Default.Circle,
                       contentDescription = null,
                       modifier = Modifier.size(12.dp),
                       tint = if (staff.active) Color(0xFF4CAF50) else Color(0xFFE57373)
                   )
                   Spacer(modifier = Modifier.height(4.dp))
                   Text(
                       text = if(staff.active) "Active" else "Inactive",
                       style = MaterialTheme.typography.labelSmall,
                       color = MaterialTheme.colorScheme.onSurfaceVariant
                   )
                }
            }
        }
    }
}
