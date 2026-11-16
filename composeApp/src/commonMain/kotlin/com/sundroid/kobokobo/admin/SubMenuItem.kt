package com.sundroid.kobokobo.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.ripple
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.sundroid.kobokobo.app.Route
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.branch
import inventrar.composeapp.generated.resources.business
import inventrar.composeapp.generated.resources.cash
import inventrar.composeapp.generated.resources.department
import inventrar.composeapp.generated.resources.eye_icon
import inventrar.composeapp.generated.resources.grocery
import inventrar.composeapp.generated.resources.inventory
import inventrar.composeapp.generated.resources.staff
import kotlinx.coroutines.delay
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource

// Data classes to represent our menu structure
data class SubMenuItem(val title: String, val onClick: () -> Unit = {})
data class MenuItem(val title: String, val icon: DrawableResource = Res.drawable.eye_icon, val subItems: List<SubMenuItem>)

@Suppress("UnusedBoxWithConstraintsScope")
@Composable
fun TwoStepMenuScreen() {
    val userViewModel: com.sundroid.kobokobo.login.presentation.UserViewModel = org.koin.compose.viewmodel.koinViewModel()
    val userState by userViewModel.state.collectAsStateWithLifecycle()
    val features = userState.currentUser?.businessFeatures ?: emptyList()
    val hasSalesFeature = features.any { it.lowercase() == "sales" }
    val hasHotelFeature = features.any { it.lowercase() == "hotel & lodgings" || it.lowercase() == "hotels & lodgings" || it.lowercase() == "hotels and lodgings" }
    val hasLaundryFeature = features.any { it.lowercase() == "laundry" }
    val hasServicesFeature = features.any { it.lowercase() == "services" }

    // Menu Items Definition
    val menuItems = buildList {
        if (hasSalesFeature) {
            add(
                MenuItem(
                    title = "Product Catalog",
                    subItems = listOf(
                        SubMenuItem("All Products", onClick = { NavigationViewModel.businessNavController?.navigate(Route.ProductListRoute) }),
                        SubMenuItem("Add New Product", onClick = { NavigationViewModel.businessNavController?.navigate(Route.AddProductRoute) }),
                        SubMenuItem("Categories", onClick = { NavigationViewModel.businessNavController?.navigate(Route.CategoryListRoute) }),
                        SubMenuItem("Add Category", onClick = { NavigationViewModel.businessNavController?.navigate(Route.AddCategory) }),
                    ),
                    icon = Res.drawable.grocery
                )
            )
        }
        
        add(
            MenuItem(
                title = "Staff Management",
                subItems = listOf(
                    SubMenuItem("Manage Roles", onClick = { NavigationViewModel.businessNavController?.navigate(Route.RoleManagementRoute) }),
                    SubMenuItem("Add Staff", onClick = { NavigationViewModel.businessNavController?.navigate(Route.AddStaffRoute) }),
                    SubMenuItem("View Staff List", onClick = { NavigationViewModel.businessNavController?.navigate(Route.StaffListRoute) }),
                ),
                icon = Res.drawable.staff
            )
        )
        
        add(
            MenuItem(
                title = "Payment Methods",
                subItems = listOf(
                    SubMenuItem("Payment Methods", onClick = { NavigationViewModel.businessNavController?.navigate(Route.PaymentMethodsRoute) }),
                ),
                icon = Res.drawable.cash
            )
        )
        
        add(
            MenuItem(
                title = "Departments",
                subItems = listOf(
                    SubMenuItem("View Departments", onClick = { NavigationViewModel.businessNavController?.navigate(Route.DepartmentListRoute) }),
                    SubMenuItem("Add Department", onClick = { NavigationViewModel.businessNavController?.navigate(Route.CreateEditDepartmentRoute) }),
                ),
                icon = Res.drawable.department
            )
        )
        
        add(
            MenuItem(
                title = "Branches",
                subItems = listOf(
                    SubMenuItem("Manage Branches", onClick = { NavigationViewModel.businessNavController?.navigate(Route.BranchManagementRoute) }),
                ),
                icon = Res.drawable.branch
            )
        )

        if (hasHotelFeature) {
            add(
                MenuItem(
                    title = "Hotel Management",
                    subItems = listOf(
                        SubMenuItem("Room Management", onClick = { NavigationViewModel.businessNavController?.navigate(Route.RoomManagementRoute) }),
                    ),
                    icon = Res.drawable.business
                )
            )
        }

        add(
            MenuItem(
                title = "Audit & Logs",
                subItems = listOf(
                    SubMenuItem("System Audit Trail", onClick = { NavigationViewModel.businessNavController?.navigate(Route.AuditTrailRoute) }),
                ),
                icon = Res.drawable.inventory
            )
        )

        if (hasLaundryFeature) {
            add(
                MenuItem(
                    title = "Laundry Management",
                    subItems = listOf(
                        SubMenuItem("Materials & Prices", onClick = { NavigationViewModel.businessNavController?.navigate(Route.LaundryMaterialListRoute) }),
                    ),
                    icon = Res.drawable.inventory // Using inventory icon since there's no specific laundry one imported
                )
            )
        }

        if (hasServicesFeature) {
            add(
                MenuItem(
                    title = "Services Management",
                    subItems = listOf(
                        SubMenuItem("Services Prices", onClick = { NavigationViewModel.businessNavController?.navigate(Route.ServicesMaterialListRoute) }),
                    ),
                    icon = Res.drawable.inventory
                )
            )
        }
    }

    var expandedItem by remember { mutableStateOf<MenuItem?>(null) }
    
    // Animation state for staggered entry
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        isVisible = true
    }

    Scaffold(
        containerColor = MaterialTheme.colorScheme.background
    ) { paddingValues ->
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
        ) {
            val width = maxWidth
            // Responsive logic: 1 column for < 600dp, 2 for < 900dp, 3 for > 900dp
            val columns = when {
                width < 600.dp -> 1
                width < 1100.dp -> 2
                else -> 3
            }

            AnimatedVisibility(
                visible = isVisible,
                enter = fadeIn(tween(500)) + slideInVertically(tween(500)) { it / 2 }
            ) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(columns),
                    horizontalArrangement = Arrangement.spacedBy(16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                   items(menuItems) { item ->
                       MenuItemCard(
                           menuItem = item,
                           isExpanded = expandedItem == item,
                           onExpand = {
                               expandedItem = if (expandedItem == item) null else item
                           }
                       )
                   }
                }
            }
        }
    }
}

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    isExpanded: Boolean,
    onExpand: () -> Unit
) {
    val cardColor = MaterialTheme.colorScheme.surface
    val expandedColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
    
    // Animate container color
    val containerColor by androidx.compose.animation.animateColorAsState(
        if (isExpanded) expandedColor else cardColor, label = "cardColor"
    )

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(
                animationSpec = spring(
                    dampingRatio = Spring.DampingRatioLowBouncy,
                    stiffness = Spring.StiffnessLow
                )
            ),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = containerColor),
        elevation = CardDefaults.cardElevation(
            defaultElevation = if (isExpanded) 8.dp else 2.dp,
            pressedElevation = 4.dp
        ),
        onClick = onExpand
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    // Icon Container
                    Box(
                        modifier = Modifier
                            .size(48.dp)
                            .clip(CircleShape)
                            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(menuItem.icon),
                            contentDescription = null,
                            modifier = Modifier.size(24.dp),
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.primary)
                        )
                    }
                    Spacer(modifier = Modifier.width(16.dp))
                    Text(
                        text = menuItem.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        ),
                        color = MaterialTheme.colorScheme.onSurface 
                    )
                }
                
                // Chevron Animation
                val rotation by androidx.compose.animation.core.animateFloatAsState(
                    targetValue = if (isExpanded) 180f else 0f, label = "rotation"
                )
                Icon(
                    imageVector = Icons.Default.KeyboardArrowDown,
                    contentDescription = null,
                    modifier = Modifier.rotate(rotation),
                    tint = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(tween(300)) + fadeIn(tween(300)),
                exit = shrinkVertically(tween(300)) + fadeOut(tween(200))
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 16.dp)
                ) {
                    Divider(color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.5f))
                    Spacer(modifier = Modifier.height(8.dp))
                    
                    menuItem.subItems.forEachIndexed { index, subItem ->
                        SubMenuItemView(subItem = subItem, index = index)
                    }
                }
            }
        }
    }
}

@Composable
fun SubMenuItemView(
    subItem: SubMenuItem,
    index: Int
) {
    // Staggered appearance for sub-items
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        delay(index * 50L) // Stagger delay
        isVisible = true
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInVertically { it / 2 } + fadeIn(),
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .clickable(
                    interactionSource = remember { MutableInteractionSource() },
                    indication = ripple(color = MaterialTheme.colorScheme.primary),
                    onClick = subItem.onClick
                )
                .padding(vertical = 12.dp, horizontal = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = subItem.title,
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Medium
                ),
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.9f)
            )
        }
    }
}