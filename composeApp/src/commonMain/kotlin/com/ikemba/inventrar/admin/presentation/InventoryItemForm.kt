package com.ikemba.inventrar.admin.presentation

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.* // Using Material 3 components
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.login.presentation.UserViewModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.person
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview



@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CreateInventoryForm(
    modifier: Modifier = Modifier,
    adminViewModel: AdminViewModel,
    userViewModel: UserViewModel,
    onImageSelected: (platformSpecificImageUri: Any?) -> Unit ={}
) {




   val formState = adminViewModel.inventoryItemState.collectAsStateWithLifecycle()
    val state = adminViewModel.state.collectAsStateWithLifecycle()

    // State for validation errors
    var skuError by remember { mutableStateOf(false) }
    var itemNameError by remember { mutableStateOf(false) }
    var orgIdError by remember { mutableStateOf(false) }
    var createdByError by remember { mutableStateOf(false) }

    // Animation for button press
    var buttonScale by remember { mutableStateOf(1f) }
    val animatedButtonScale by animateFloatAsState(buttonScale)

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Create New Inventory Item",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )

        // SKU
        OutlinedTextField(
            value = formState.value.sku,
            onValueChange = {
                adminViewModel.updateSku(it)
              //  sku = it
                skuError = false // Clear error on change
            },
            label = { Text("SKU *") },
            isError = skuError,
            supportingText = { if (skuError) Text("SKU cannot be empty") },
            modifier = Modifier.fillMaxWidth(),
        )

        // Item Name
        OutlinedTextField(
            value = formState.value.itemName,
            onValueChange = {
                adminViewModel.updateItemName(it)
                itemNameError = false
            },
            label = { Text("Item Name *") },
            isError = itemNameError,
            supportingText = { if (itemNameError) Text("Item name cannot be empty") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Category
        OutlinedTextField(
            value = formState.value.category,
            onValueChange = { adminViewModel.updateCategory(it) },
            label = { Text("Category") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Unit of Measure
        OutlinedTextField(
            value = formState.value.unitOfMeasure,
            onValueChange = { adminViewModel.updateUnitOfMeasure(it) },
            label = { Text("Unit of Measure") },
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        // Quantity (Numeric)
        OutlinedTextField(
            value = formState.value.quantity.toString(),
            onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) adminViewModel.updateQuantity(it.toIntOrNull() ?: 0) },
            label = { Text("Quantity") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Purchase Price (Numeric)
            OutlinedTextField(
                value = formState.value.purchasePrice,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) adminViewModel.updatePurchasePrice(it.toDoubleOrNull() ?: 0.0)},
                label = { Text("Purchase Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors()
            )

            // Selling Price (Numeric)
            OutlinedTextField(
                value = formState.value.sellingPrice,
                onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) adminViewModel.updateSellingPrice(it.toDoubleOrNull() ?: 0.0) } ,
                label = { Text("Selling Price") },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                modifier = Modifier.weight(1f),
                colors = OutlinedTextFieldDefaults.colors()
            )
        }

        // Reorder Level (Numeric)
        OutlinedTextField(
            value = formState.value.reorderLevel,
            onValueChange = { if (it.matches(Regex("^\\d*\\.?\\d*\$"))) adminViewModel.updateReorderLevel(it)},
            label = { Text("Reorder Level") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth(),
            colors = OutlinedTextFieldDefaults.colors()
        )


        // Image Selection and Preview
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(1.dp, MaterialTheme.colorScheme.outline, RoundedCornerShape(12.dp))
                .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f))
                .clickable {
                    // Trigger platform-specific image picker
                    // The result (e.g., URI, path) would be passed to onImageSelected
                    onImageSelected(null) // Placeholder: pass null or a dummy URI for now
                }
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            AnimatedVisibility(
                visible = formState.value.imageUrl != null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(150.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(MaterialTheme.colorScheme.surfaceVariant),
                    contentAlignment = Alignment.Center
                ) {
                    formState.value.imageUrl?.let { url ->
                        Image(
                            painter = painterResource(Res.drawable.person), // Use the platform-specific painter
                            contentDescription = "Selected Image",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        IconButton(
                            onClick = { formState.value.imageUrl = null }, // Clear image
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(4.dp)
                                .clip(RoundedCornerShape(50))
                                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.7f))
                        ) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear Image",
                                tint = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }
                }
            }

            AnimatedVisibility(
                visible = formState.value.imageUrl == null,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Add Photo",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.size(48.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Tap to select item image",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }



        CustomText(state.value.errorMessage)

        Spacer(modifier = Modifier.height(16.dp))

        // Create Button
        Button(
            onClick = {
                skuError = formState.value.sku.isBlank()
                itemNameError = formState.value.itemName.isBlank()


               // if (!skuError && !itemNameError && !orgIdError && !createdByError) {
                    // Simulate button press animation
                    buttonScale = 0.95f

                adminViewModel.addInventoryItem(userViewModel.getUser()!!)


                //}
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(56.dp)
                .scale(animatedButtonScale),
            shape = RoundedCornerShape(12.dp),
        ) {
            Text(text = "Create Inventory Item", style = MaterialTheme.typography.titleMedium)
        }
    }
}



