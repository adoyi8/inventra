package com.sundroid.kobokobo.department

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
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sundroid.kobokobo.admin.data.domain.model.Department
import com.sundroid.kobokobo.core.presentation.components.CustomText
import kotlinx.coroutines.delay

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentListScreen(
    viewModel: DepartmentViewModel,
    onNavigateToCreateEdit: () -> Unit,
    title: MutableState<String>
) {
    val departments by viewModel.departments.collectAsState()
    
    LaunchedEffect(Unit){
        title.value = "Departments Management"
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    viewModel.startCreating()
                    onNavigateToCreateEdit()
                },
                containerColor = MaterialTheme.colorScheme.primary,
                contentColor = MaterialTheme.colorScheme.onPrimary
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Department")
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

            if (departments.isEmpty()) {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            Icons.Default.Business, 
                            contentDescription = null, 
                            modifier = Modifier.size(64.dp), 
                            tint = MaterialTheme.colorScheme.outline
                        )
                        Spacer(Modifier.height(16.dp))
                        CustomText(
                            "No Departments Found", 
                            style = 18.sp, 
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                        CustomText(
                            "Add a new department to get started.", 
                            style = 14.sp, 
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                        )
                    }
                }
            } else {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 72.dp)
                ) {
                    itemsIndexed(departments, key = { _, item -> item.id }) { index, department ->
                        DepartmentItem(
                            department = department,
                            index = index,
                            onEdit = {
                                viewModel.startEditing(department)
                                onNavigateToCreateEdit()
                            },
                            onDelete = {
                                viewModel.deleteDepartment(department)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun DepartmentItem(
    department: Department,
    index: Int,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
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
                .clickable { onEdit() },
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    // Icon
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                         Text(
                            text = department.name.take(1).uppercase(),
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    }
                    
                    // Actions
                    Row {
                        IconButton(onClick = onEdit) {
                            Icon(Icons.Default.Edit, contentDescription = "Edit", tint = MaterialTheme.colorScheme.primary)
                        }
                        IconButton(onClick = onDelete) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
                
                Text(
                    text = department.name,
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                    color = MaterialTheme.colorScheme.onSurface
                )
                
                if (department.description.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = department.description,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        maxLines = 2
                    )
                }
            }
        }
    }
}