package com.ikemba.inventrar.user.presentation

/*
 * App.kt
 * This file contains the entire application logic for the Shrine-inspired profile page,
 * updated to use Material 3 components.
 */

import androidx.compose.animation.*
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
// Material 3 imports
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.ikemba.inventrar.business.AnimatedButton
import com.ikemba.inventrar.business.RoomListContainer

import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.theme.AppTheme

import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.add
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShrineProfileApp(userViewModel: UserViewModel) {
    // M3: Use DrawerState for the navigation drawer
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    var currentScreen by remember { mutableStateOf("Profile") }
    AppTheme {
        // M3: Use ModalNavigationDrawer for the drawer layout
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        Text("Shrine", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        NavigationDrawerItem(
                            label = { Text("Settings") },
                            selected = false,
                            onClick = { /* Handle click */ },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { Text("Help") },
                            selected = false,
                            onClick = { /* Handle click */ },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        NavigationDrawerItem(
                            label = { Text("Logout") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    userViewModel.showShowConfirmLogout()
                                    drawerState.close()
                                }
                               },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                    }
                }
            }
        ) {
            // M3: Scaffold is now used inside the main content area
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Profile") },
                        navigationIcon = {
                            IconButton(onClick = {
                                scope.launch {
                                    drawerState.open()
                                }
                            }) {
                                Icon(Icons.Default.Menu, contentDescription = "Menu")
                            }
                        },
                        actions = {
                            IconButton(onClick = {}) {
                                Icon(Icons.Default.MoreVert, contentDescription = "More")
                            }
                        },
                        // M3: Colors are set via a colors object
                        colors = TopAppBarDefaults.topAppBarColors(
                            containerColor = MaterialTheme.colorScheme.primaryContainer,
                            titleContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            actionIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer,
                            navigationIconContentColor = MaterialTheme.colorScheme.onPrimaryContainer
                        )
                    )
                },
                // M3: BottomNavigation is now NavigationBar
                bottomBar = {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.primaryContainer,
                        contentColor = MaterialTheme.colorScheme.onPrimaryContainer
                    ) {
                        // M3: BottomNavigationItem is now NavigationBarItem
                        NavigationBarItem(
                            icon = { Icon(painterResource(Res.drawable.add), contentDescription = "Profile", modifier=Modifier.size(24.dp)) },
                            label = { Text("Profile") },
                            selected = currentScreen == "Profile",
                            onClick = { currentScreen = "Profile" }
                        )
                        NavigationBarItem(
                            icon = { Icon(painterResource(Res.drawable.add), contentDescription = "Businesses", modifier=Modifier.size(24.dp)) },
                            label = { Text("Businesses") },
                            selected = currentScreen == "Businesses",
                            onClick = { currentScreen = "Businesses" }
                        )
                    }
                },
                floatingActionButton = {
                    AnimatedVisibility(
                        visible = currentScreen == "Businesses"

                    ) {
                        FloatingActionButton({ currentScreen = "CreateBusiness" }) {
                            Icon(imageVector =  (Icons.Default.Add), contentDescription = "")
                        }
                    }
                }

            ) { paddingValues ->
                AnimatedContent(
                    targetState = currentScreen,
                    transitionSpec = {
                        fadeIn(animationSpec = tween(300)) togetherWith
                                fadeOut(animationSpec = tween(300))
                    },
                    modifier = Modifier.padding(paddingValues)
                ) { screen ->
                    when (screen) {
                        "Profile" -> ProfileScreen()
                        "Businesses" ->  RoomListContainer(userViewModel)
                        "CreateBusiness" ->  BusinessCreationForm()
                    }
                }
            }
        }
    }
}

@Composable
fun ProfileScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(modifier = Modifier.height(24.dp))
        Image(
            painter = painterResource(Res.drawable.add),
            contentDescription = "Profile Picture",
            modifier = Modifier
                .size(120.dp)
                .clip(CircleShape)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Text("Jane Doe", style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        Text("Fashion Designer | Traveler | Foodie", style = MaterialTheme.typography.bodyLarge, color = Color.Gray)
        Spacer(modifier = Modifier.height(24.dp))
        Row(
            horizontalArrangement = Arrangement.SpaceEvenly,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("1.2K", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Posts", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("5.6K", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Followers", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text("3.2K", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold)
                Text("Following", style = MaterialTheme.typography.bodyMedium, color = Color.Gray)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessCreationForm() {
    var businessName by remember { mutableStateOf("") }
    var address by remember { mutableStateOf("") }

    // Sample data for dropdowns
    val businessTypes = listOf("Retail", "Technology", "Hospitality", "Healthcare")
    val countries = listOf("Nigeria", "USA", "Canada", "UK")
    val states = mapOf("Nigeria" to listOf("Lagos", "Abuja", "Kano"), "USA" to listOf("California", "New York", "Texas"))
    val cities = mapOf("Lagos" to listOf("Ikeja", "Victoria Island"), "Abuja" to listOf("Garki", "Wuse"))

    var selectedCountry by remember { mutableStateOf(countries[0]) }
    var selectedState by remember { mutableStateOf(states[selectedCountry]?.get(0) ?: "") }
    var selectedCity by remember { mutableStateOf(cities[selectedState]?.get(0) ?: "") }

    // Update states when country changes
    LaunchedEffect(selectedCountry) {
        selectedState = states[selectedCountry]?.get(0) ?: ""
    }
    // Update cities when state changes
    LaunchedEffect(selectedState) {
        selectedCity = cities[selectedState]?.get(0) ?: ""
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
            .verticalScroll(rememberScrollState()), // Make form scrollable
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        AnimatedVisibility(
            visible = true, // Can be toggled
            enter = slideInVertically(initialOffsetY = { -40 }) + fadeIn(),
            exit = slideOutVertically() + fadeOut()
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                OutlinedTextField(
                    value = businessName,
                    onValueChange = { businessName = it },
                    label = { Text("Business Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(Modifier.height(16.dp))

                // Business Type Dropdown
                DropdownField(
                    label = "Business Type",
                    options = businessTypes,
                    selectedOption = businessTypes[0],
                    onOptionSelected = { /* Update state */ }
                )
                Spacer(Modifier.height(16.dp))

                // Country Dropdown
                DropdownField("Country", countries, selectedCountry) { selectedCountry = it }
                Spacer(Modifier.height(16.dp))

                // State Dropdown
                DropdownField("State", states[selectedCountry] ?: emptyList(), selectedState) { selectedState = it }
                Spacer(Modifier.height(16.dp))

                // City Dropdown
                DropdownField("City", cities[selectedState] ?: emptyList(), selectedCity) { selectedCity = it }
                Spacer(Modifier.height(16.dp))

                OutlinedTextField(
                    value = address,
                    onValueChange = { address = it },
                    label = { Text("Address") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                Spacer(Modifier.height(24.dp))

                Button(
                    onClick = { /* Handle form submission */ },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text("Create Business", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropdownField(
    label: String,
    options: List<String>,
    selectedOption: String,
    onOptionSelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    var currentSelection by remember { mutableStateOf(selectedOption) }

    // Update internal state if the external selectedOption changes
    LaunchedEffect(selectedOption) {
        currentSelection = selectedOption
    }

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = { expanded = !expanded }
    ) {
        OutlinedTextField(
            value = currentSelection,
            onValueChange = {},
            readOnly = true,
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
            modifier = Modifier.fillMaxWidth().menuAnchor()
        )
        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = { expanded = false }
        ) {
            options.forEach { option ->
                DropdownMenuItem(
                    text = { Text(option) },
                    onClick = {
                        currentSelection = option
                        onOptionSelected(option)
                        expanded = false
                    }
                )
            }
        }
    }
}



