package com.ikemba.inventrar.dashboard.presentation.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Badge
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.ikemba.inventrar.core.presentation.PrimaryColor
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.domain.Category

import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.utils.Util.currencyFormat
import com.ikemba.inventrar.inventory.data.domain.model.Product
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.scanner
import inventrar.composeapp.generated.resources.search
import org.jetbrains.compose.resources.painterResource


@OptIn(ExperimentalFoundationApi::class)
@Composable
fun POSSCreenBody(
    viewModel: DashboardViewModel,
    modifier: Modifier = Modifier,
    isMobile: Boolean, // New parameter
    onShowCart: () -> Unit // New parameter
) {

    val state by viewModel.state.collectAsStateWithLifecycle()

    val selectedCategory = remember { mutableStateOf<Category>(Category("1", "1", "1")) }
    var searchValue by remember { mutableStateOf("") }

    Box(modifier = modifier) {
        Column(modifier = Modifier
            .verticalScroll(rememberScrollState()).heightIn(min = 0.dp, max = 5000.dp)
        ) {
            OutlinedTextField(
                value = searchValue,
                onValueChange = { it ->
                    searchValue = it
                    viewModel.searchItems(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                placeholder = { Text("Search product list") },
                leadingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.search),
                        contentDescription = "",
                        modifier = Modifier.size(25.dp)
                    )
                },
                trailingIcon = {
                    Image(
                        painter = painterResource(Res.drawable.scanner),
                        contentDescription = ""
                    )
                }
            )

            Column(Modifier.fillMaxWidth().fillMaxHeight().background(Color(0xFFe4e4e4))) {
                CustomText(
                    "Categories",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp),
                    size = 18.sp
                )
                // --- Enhancement: Animated Category Selection ---
                Row(modifier = Modifier.horizontalScroll(rememberScrollState())) {
                    state.categories.forEach { category ->

                        val isSelected = selectedCategory.value == category
                        val backgroundColor by animateColorAsState(
                            if (isSelected) MaterialTheme.colorScheme.primary else Color.White
                        )
                        val textColor by animateColorAsState(
                            if (isSelected) Color.White else Color.Black
                        )

                        Text(
                            text = category.categoryName,
                            modifier = Modifier
                                .padding(8.dp)
                                .clickable {
                                    selectedCategory.value = category
                                    viewModel.selectCategory(category.categoryId)
                                }
                                .background(backgroundColor, RoundedCornerShape(8.dp))
                                .border(1.dp, Color.Gray, RoundedCornerShape(8.dp))
                                .padding(12.dp),
                            color = textColor
                        )
                    }
                }
                // --- End Enhancement ---

                CustomText(
                    "Product List",
                    color = Color.Black,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.padding(8.dp),
                    size = 18.sp
                )
                // Product List with Animated Appearance
                val gridState = rememberLazyGridState()
                Column(
                    modifier = Modifier
                        //.heightIn(min = 0.dp, max = 5000.dp).verticalScroll(rememberScrollState())
                ) {
                    // --- Responsive Grid ---
                    LazyVerticalGrid(
                        // Use Adaptive columns for responsiveness
                        columns = GridCells.Adaptive(minSize = 150.dp),
                        state = gridState,
                        modifier = Modifier.padding(end = 16.dp),
                        contentPadding = PaddingValues(bottom = 80.dp) // Add padding for FABs
                    ) {
                        items(state.visibleProducts, key = { it.id }) { item ->
                            // --- Enhancement: Animate Item Appearance ---
                            this@Column.AnimatedVisibility(
                                visible = true,
                                enter = fadeIn(animationSpec = tween(300)) +
                                        slideInVertically(animationSpec = tween(300), initialOffsetY = { it / 2 }),
                                modifier = Modifier.animateItemPlacement() // Key for grid changes
                            ) {
                                ProductCard(viewModel = viewModel, product = item)
                            }
                        }
                    }
//                    VerticalScrollbar(
//                        modifier = Modifier.align(Alignment.CenterEnd).padding(start = 16.dp).fillMaxHeight(),
//                        adapter = rememberScrollbarAdapter(gridState) // <-- Use gridState
//                    )
                }
            }
        }

        // --- Modified FABs ---
        Column(
            modifier = Modifier.align(Alignment.BottomEnd).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // 1. Refresh FAB (as before)
            FloatingActionButton(
                onClick = { viewModel.getProducts() },
                containerColor = PrimaryColor // Use your PrimaryColor
            ) {
                Icon(Icons.Filled.Refresh, contentDescription = "Refresh", tint = Color.White)
            }

            // 2. New Cart FAB (Mobile Only)
            if (isMobile) {
                BadgedBox(
                    badge = {
                        // Show badge only if cart is not empty
                        val itemCount = state.cartItems.sumOf { it.quantity }
                        if (itemCount > 0) {
                            Badge { Text("$itemCount") }
                        }
                    }
                ) {
                    FloatingActionButton(
                        onClick = onShowCart, // Triggers the bottom sheet
                        containerColor = MaterialTheme.colorScheme.secondary
                    ) {
                        Icon(Icons.Default.ShoppingCart, "View Cart", tint = Color.White)
                    }
                }
            }
        }
        // --- End Modified FABs ---
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductCard(viewModel: DashboardViewModel, product: Product) {
    Card(
        modifier = Modifier
            .padding(8.dp)
            .clickable { viewModel.addItemToCart(product) } // Simplified click handler
            .animateContentSize(animationSpec = tween(300)),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White)
    ) {
        Column(modifier = Modifier.padding(0.dp)) {
            CustomText(text = product.name, fontWeight = FontWeight.Light, modifier = Modifier.padding(horizontal = 8.dp), textAlign = TextAlign.Left, color = Color.DarkGray)
            Row(Modifier.fillMaxWidth().padding(horizontal = 8.dp), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                CustomText(text = "${currencyFormat(product.unitPrice)}", size = 14.sp, fontWeight = FontWeight.SemiBold, color = Color.DarkGray)
                IconButton(onClick = {
                    viewModel.addItemToCart(product)
                }, )
                {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "",
                        tint = PrimaryColor // Use your PrimaryColor
                    )
                }
            }
        }
    }
}