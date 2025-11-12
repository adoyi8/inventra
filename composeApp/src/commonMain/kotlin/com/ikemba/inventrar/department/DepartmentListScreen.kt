package com.ikemba.inventrar.department

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikemba.inventrar.admin.data.domain.model.Department
import com.ikemba.inventrar.core.presentation.components.CustomText

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DepartmentListScreen(
    viewModel: DepartmentViewModel,
    onNavigateToCreateEdit: () -> Unit,
    title: MutableState<String>
) {
    val departments by viewModel.departments.collectAsState()
    LaunchedEffect(true){
        title.value = "Departments"
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(onClick = {
                viewModel.startCreating()
                onNavigateToCreateEdit()
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Department")
            }
        }
    ) { paddingValues ->
        if (departments.isEmpty()) {
            Box(
                modifier = Modifier.fillMaxSize().padding(paddingValues),
                contentAlignment = Alignment.Center
            ) {
                CustomText("No departments found.", size = 16.sp)
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(departments, key = { it.id }) { department ->
                    DepartmentItem(
                        department = department,
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

@Composable
private fun DepartmentItem(
    department: Department,
    onEdit: () -> Unit,
    onDelete: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onEdit() }
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                CustomText(department.name, size = 16.sp)
                if (department.description.isNotEmpty()) {
                    CustomText(
                        department.description,
                        size = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
            Spacer(Modifier.width(16.dp))
            IconButton(onClick = onDelete) {
                Icon(
                    Icons.Default.Delete,
                    contentDescription = "Delete ${department.name}",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}