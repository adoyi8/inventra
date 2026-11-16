package com.sundroid.kobokobo.inventory.presentation

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.inventory.data.domain.model.Category
import com.sundroid.kobokobo.theme.TextSize
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CategoryListScreen(
    viewModel: CategoryListViewModel = koinViewModel(),
    onNavigateToAddCategory: () -> Unit,
    onNavigateToCategoryDetail: (String) -> Unit,
    title: MutableState<String>
) {
    val uiState by viewModel.uiState.collectAsState()

    LaunchedEffect(true){
        title.value = "Categories"
    }

    Scaffold(
        floatingActionButton = {
            FloatingActionButton(
                onClick = onNavigateToAddCategory
            ) {
                Icon(Icons.Default.Add, contentDescription = "Add Category")
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
                CustomText(
                    text = it,
                    color = MaterialTheme.colorScheme.error,
                    modifier = Modifier.padding(vertical = 8.dp)
                )
            }

            LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                items(uiState.categories) { category ->
                    CategoryCard(
                        category = category,
                        onClick = { onNavigateToCategoryDetail(category.id) }
                    )
                }
            }
        }
    }
}

@Composable
fun CategoryCard(
    category: Category,
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
            CustomText(
                text = category.name,
                style =  TextSize.titleMedium
            )
            if (category.description.isNotBlank()) {
                Spacer(modifier = Modifier.height(4.dp))
                CustomText(
                    text = category.description,
                    style = TextSize.bodyMedium
                )
            }
        }
    }
}
