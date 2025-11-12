package com.ikemba.inventrar.app

import POScreen
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
//import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import com.ikemba.inventrar.admin.TwoStepMenuScreen
import com.ikemba.inventrar.admin.presentation.AdminViewModel
import com.ikemba.inventrar.admin.presentation.CreateInventoryForm
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.business.presentation.BusinessViewModel
import com.ikemba.inventrar.business.presentation.businessList.BusinessListScreen
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.presentation.components.ConfirmLogout
import com.ikemba.inventrar.dashboard.utils.Util.APP_NAME
import com.ikemba.inventrar.dropdowndata.presentation.DropDownSettingsViewModel
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.theme.AppTheme
import com.ikemba.inventrar.theme.BlueProfileTheme
import com.ikemba.inventrar.user.presentation.ProfileScreen

import com.ikemba.inventrar.utils.StringUtil
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.add
import inventrar.composeapp.generated.resources.business
import inventrar.composeapp.generated.resources.person
import inventrar.composeapp.generated.resources.scanner
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun GlobalNavGraph(dashboardViewModel: MutableState<DashboardViewModel?>? = null, userViewModel: UserViewModel, businessViewModel: BusinessViewModel = koinViewModel(), dropDownSettingsViewModel: DropDownSettingsViewModel = koinViewModel(), adminViewModel: AdminViewModel = koinViewModel(), googleAuthUiProvider: GoogleAuthUiProvider, businessListViewModel: BusinessListViewModel = koinViewModel()  ) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userState by userViewModel.state.collectAsStateWithLifecycle()
    val navController = rememberNavController()
    val title = remember{ mutableStateOf("")}

    BlueProfileTheme {
        // M3: Use ModalNavigationDrawer for the drawer layout
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.widthIn(max = 280.dp),
                    drawerContainerColor = MaterialTheme.colorScheme.primaryContainer
                ) {
                    Column(
                        modifier = Modifier
                            .padding(16.dp)
                    ) {
                        CustomText(APP_NAME, fontWeight = FontWeight.Bold)
                        Spacer(modifier = Modifier.height(24.dp))
                        NavigationDrawerItem(
                            label = { CustomText("Profile") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Route.UserProfileRoute)
                                }
                                },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { CustomText("Businesses") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                navController.navigate(Route.BusinessListScreenRoute)
                                } },
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        NavigationDrawerItem(
                            label = { CustomText("Logout") },
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
                        title = { CustomText(title.value) },
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
                            icon = { Icon(painterResource(Res.drawable.person), contentDescription = "Profile", modifier= Modifier.size(24.dp)) },
                            label = { CustomText("Profile") },
                            selected = true,
                            onClick = { navController.navigate(Route.UserProfileRoute)}
                        )
                        NavigationBarItem(
                            icon = { Icon(painterResource(Res.drawable.business), contentDescription = "Businesses", modifier= Modifier.size(24.dp)) },
                            label = { CustomText("Businesses") },
                            selected = title.value == "Businesses",
                            onClick = { navController.navigate(Route.BusinessListScreenRoute)}
                        )
                    }
                },


            ) { paddingValues ->


                NavHost(
                    modifier = Modifier.padding(paddingValues),
                    navController = navController,
                    startDestination = Route.BookGraph
                )
                {

                    navigation<Route.BookGraph>(
                        startDestination = Route.BusinessListScreenRoute
                    ) {

                        composable<Route.UserProfileRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Profile"
                            ProfileScreen()
                        }
                        composable<Route.BusinessListScreenRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Businesses"
                            BusinessListScreen(userViewModel = userViewModel, viewModel = businessListViewModel)
                        }
                    }

                }
                }
            }
    }
    ConfirmLogout(userViewModel)
}
