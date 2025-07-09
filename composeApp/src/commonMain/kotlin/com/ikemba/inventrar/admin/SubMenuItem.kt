package com.ikemba.inventrar.admin

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.shrinkVertically
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.ikemba.inventrar.app.Route
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.eye_icon
import org.jetbrains.compose.resources.DrawableResource
import org.jetbrains.compose.resources.painterResource


// Data classes to represent our menu structure
data class SubMenuItem(val title: String, val onClick: () -> Unit = {})
data class MenuItem(val title: String, val icon: DrawableResource = Res.drawable.eye_icon , val subItems: List<SubMenuItem>)

@Composable
fun TwoStepMenuScreen() {
    // Sample data for the menu
    val menuItems = listOf(
        MenuItem(
            title = "Profile",
            subItems = listOf(SubMenuItem("Edit Profile"), SubMenuItem("Change Password"), SubMenuItem("Privacy Settings"))
        ),
        MenuItem(
            title = "Inventory",
            subItems = listOf(SubMenuItem("Add Item", onClick = { NavigationViewModel.navController?.navigate(
                Route.AddItemToInventoryRoute)}), SubMenuItem("Email Notifications"), SubMenuItem("SMS Alerts"))
        ),
        MenuItem(
            title = "Appearance",
            subItems = listOf(SubMenuItem("Theme"), SubMenuItem("Font Size"), SubMenuItem("Dark Mode"))
        ),
        MenuItem(
            title = "Support",
            subItems = listOf(SubMenuItem("Help Center"), SubMenuItem("Contact Us"), SubMenuItem("FAQ"))
        )
    )

    var expandedItem by remember { mutableStateOf<MenuItem?>(null) }

    Scaffold(
        topBar = {
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
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

@Composable
fun MenuItemCard(
    menuItem: MenuItem,
    isExpanded: Boolean,
    onExpand: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onExpand),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Image(painter = painterResource(menuItem.icon), contentDescription = "${menuItem.title} Icon")
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = menuItem.title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.weight(1f))
                Icon(
                    imageVector = if (isExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                    contentDescription = if (isExpanded) "Collapse" else "Expand"
                )
            }

            AnimatedVisibility(
                visible = isExpanded,
                enter = expandVertically(animationSpec = tween(durationMillis = 300)),
                exit = shrinkVertically(animationSpec = tween(durationMillis = 300))
            ) {
                Column(modifier = Modifier.padding(top = 16.dp)) {
                    menuItem.subItems.forEach { subItem ->
                        SubMenuItemView(subItem = subItem)
                    }
                }
            }
        }
    }
}

@Composable
fun SubMenuItemView(subItem: SubMenuItem) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(
                onClick = subItem.onClick

            )
            .padding(vertical = 8.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Spacer(modifier = Modifier.width(40.dp)) // For indentation
        Text(
            text = subItem.title,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}