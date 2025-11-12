package com.ikemba.inventrar.app

import AddStaffScreen
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
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.ikemba.inventrar.admin.TwoStepMenuScreen
import com.ikemba.inventrar.admin.presentation.AdminViewModel
import com.ikemba.inventrar.admin.presentation.CreateInventoryForm
import com.ikemba.inventrar.admin.presentation.rolePermission.RoleManagementScreen
import com.ikemba.inventrar.admin.presentation.rolePermission.CreateEditRoleScreen
import com.ikemba.inventrar.admin.presentation.rolePermission.RolePermissionViewModel
import com.ikemba.inventrar.admin.presentation.staff.StaffListScreen
import com.ikemba.inventrar.admin.presentation.staff.StaffDetailsScreen
import com.ikemba.inventrar.auth.GoogleAuthUiProvider
import com.ikemba.inventrar.business.presentation.AddStaffViewModel
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.business.presentation.BusinessViewModel
import com.ikemba.inventrar.core.presentation.NavigationViewModel
import com.ikemba.inventrar.core.presentation.components.CustomText
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.dashboard.presentation.components.ConfirmLogout
import com.ikemba.inventrar.department.CreateEditDepartmentScreen
import com.ikemba.inventrar.department.DepartmentListScreen
import com.ikemba.inventrar.department.DepartmentViewModel
import com.ikemba.inventrar.dropdowndata.presentation.DropDownSettingsViewModel
import com.ikemba.inventrar.inventory.presentation.CategoryFormScreen
import com.ikemba.inventrar.inventory.presentation.CategoryFormViewModel
import com.ikemba.inventrar.inventory.presentation.CategoryListScreen
import com.ikemba.inventrar.inventory.presentation.CategoryListViewModel
import com.ikemba.inventrar.inventory.presentation.LocationListViewModel
import com.ikemba.inventrar.inventory.presentation.ProductFormScreen
import com.ikemba.inventrar.inventory.presentation.ProductFormViewModel
import com.ikemba.inventrar.inventory.presentation.ProductListViewModel
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.theme.AppTheme
import com.ikemba.inventrar.transactionHistory.presentation.TransactionHistoryViewModel
import com.ikemba.inventrar.transactionHistory.presentation.TransactionScreen
import com.ikemba.inventrar.user.presentation.ProfileScreen
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.admin_panel
import inventrar.composeapp.generated.resources.person
import inventrar.composeapp.generated.resources.receipt
import inventrar.composeapp.generated.resources.scanner
import inventrar.composeapp.generated.resources.shopping_basket
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
@Preview
fun BusinessNavigationGraph(dashboardViewModel: MutableState<DashboardViewModel?>? = koinViewModel(), userViewModel: UserViewModel, businessViewModel: BusinessViewModel = koinViewModel(), dropDownSettingsViewModel: DropDownSettingsViewModel = koinViewModel(), adminViewModel: AdminViewModel = koinViewModel(), googleAuthUiProvider: GoogleAuthUiProvider, businessListViewModel: BusinessListViewModel = koinViewModel(), departmentViewModel: DepartmentViewModel = koinViewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userState by userViewModel.state.collectAsState()
    val navController = rememberNavController()
    val rolePermissionViewModel: RolePermissionViewModel = koinViewModel()
    val title = remember { mutableStateOf("Hello") }

    LaunchedEffect(true){
        NavigationViewModel.businessNavController = navController
        rolePermissionViewModel.setBusinessId(userState.currentUser?.businessId)
    }
    AppTheme {
        // M3: Use ModalNavigationDrawer for the drawer layout
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                ModalDrawerSheet(
                    modifier = Modifier.widthIn(max = 280.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp)
                    ) {
                        CustomText(
                            text = userState.currentUser?.businessName ?: "",
                            fontWeight = FontWeight.Bold,
                            size = 35.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        NavigationDrawerItem(
                            label = { CustomText("Manage") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Route.AdminMenuRoute)
                                }},
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )
                        NavigationDrawerItem(
                            label = { CustomText("Sales") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                navController.navigate(Route.POSScreen) }},
                            colors = NavigationDrawerItemDefaults.colors(unselectedContainerColor = Color.Transparent)
                        )

                        Divider(modifier = Modifier.padding(vertical = 16.dp))
                        NavigationDrawerItem(
                            label = { CustomText("Exit Business") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    userViewModel.exitBusiness()
                                }
                            },
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
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.admin_panel),
                                    contentDescription = "Admin Panel",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { CustomText("Admin Panel") },
                            selected = navController.currentDestination?.equals(Route.AdminMenuRoute)
                                ?: false,
                            onClick = { navController.navigate(Route.AdminMenuRoute) }
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.shopping_basket),
                                    contentDescription = "Sales",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { CustomText("Sales") },
                            selected = navController.currentDestination?.equals(Route.POSScreen)
                                ?: false,
                            onClick = { navController.navigate(Route.POSScreen) }
                        )
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.receipt),
                                    contentDescription = "Sales Report",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { CustomText("Sales Report") },
                            selected = navController.currentDestination?.equals(Route.TransactionHistoryScreen)
                                ?: false,
                            onClick = { navController.navigate(Route.TransactionHistoryScreen) }
                        )
                    }
                },


                ) { paddingValues ->


                NavHost(
                    navController = navController,
                    startDestination = Route.BookGraph,
                    modifier = Modifier.padding(paddingValues)
                )
                {

                    navigation<Route.BookGraph>(
                        startDestination = Route.POSScreen
                    ) {

                        composable<Route.POSScreen>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Sales"
                            POScreen(viewModel = koinViewModel<DashboardViewModel>(), userViewModel = userViewModel)
                        }
                        composable<Route.TransactionHistoryScreen>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Sales Report"
                            TransactionScreen(viewModel = koinViewModel<TransactionHistoryViewModel>())
                        }

                        composable<Route.UserProfileRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Profile"
                            ProfileScreen()
                        }
                        composable<Route.AdminMenuRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Admin Panel"
                            TwoStepMenuScreen()
                        }
                        composable<Route.BranchManagementRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Branch"
                            com.ikemba.inventrar.admin.presentation.branch.BranchManagementScreen()
                        }
                        composable<Route.ProductListRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {

                            val productListViewModel: ProductListViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            title.value = "Products"
                            com.ikemba.inventrar.inventory.presentation.ProductListScreen(
                                viewModel = productListViewModel,
                                onNavigateToAddProduct = {
                                    // Navigate to the combined form screen for Add (productId = null)
                                    NavigationViewModel.businessNavController?.navigate(Route.AddProductRoute)
                                },
                                onNavigateToProductDetail = { productId ->
                                    // Navigate to the combined form screen for Detail/Edit
                                    NavigationViewModel.businessNavController?.navigate(Route.ProductDetailRoute(productId))
                                },
                                 title = title
                            )
                        }

                        composable<Route.CategoryListRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            val categoryListViewModel: CategoryListViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            title.value = "Categories"
                            CategoryListScreen(
                                viewModel = categoryListViewModel,
                                onNavigateToAddCategory = {
                                    NavigationViewModel.businessNavController?.navigate(Route.AddCategory)
                                },
                                onNavigateToCategoryDetail = { categoryId ->
                                    // Navigate to the combined form screen for Edit
                                    NavigationViewModel.businessNavController?.navigate(
                                        Route.CategoryDetailRoute(
                                            categoryId
                                        )
                                    )
                                },
                                 title  = title
                            )
                        }
                        // Add the new AddCategoryScreen composable


                        composable<Route.AddCategory>(
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }) }
                        ) {
                            val businessId = userState.currentUser?.businessId
                            val staffId = userState.currentUser?.staffNo

                            if (businessId != null && staffId != null) {
                                val viewModel: CategoryFormViewModel = koinViewModel {
                                    // Pass null for categoryId, and the two required IDs
                                    parametersOf(null, businessId, staffId)
                                }
                                CategoryFormScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = { NavigationViewModel.businessNavController?.popBackStack() },
                                    title
                                )
                            } // ... error handling
                        }


// 2. Category Detail/Edit Route (uses form with category ID)
                        composable<Route.CategoryDetailRoute> {
                            val args = it.toRoute<Route.CategoryDetailRoute>()
                            val businessId = userState.currentUser?.businessId
                            val staffId = userState.currentUser?.staffNo

                            if (businessId != null && staffId != null) {
                                val viewModel: CategoryFormViewModel = koinViewModel {
                                    parametersOf(args.categoryId, businessId, staffId)
                                }
                                CategoryFormScreen(
                                    viewModel = viewModel,
                                    onNavigateBack = {
                                        NavigationViewModel.businessNavController?.popBackStack()
                                    },
                                    title
                                )
                            }
                        }





                        composable<Route.LocationListRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                val locationListViewModel: LocationListViewModel = koinViewModel(
                                    parameters = { parametersOf(userState.currentUser?.businessId) }
                                )
                                com.ikemba.inventrar.inventory.presentation.LocationListScreen(
                                    viewModel = locationListViewModel,
                                    onNavigateToAddLocation = { /* Navigate to add location */ },
                                    onNavigateToLocationDetail = { /* Navigate to location detail */ }
                                )
                            }


// Update Route.AddProductRoute
                        composable<Route.AddProductRoute>(
                            exitTransition = { slideOutHorizontally(targetOffsetX = { it }) },
                            popEnterTransition = { slideInHorizontally(initialOffsetX = { it }) }
                        ) {
                            val businessId = userState.currentUser?.businessId
                            val staffId = userState.currentUser?.staffNo

                            if (businessId != null && staffId != null) {
                                val productFormViewModel: ProductFormViewModel = koinViewModel {
                                    // PASS NULL FIRST for productId (index 0)
                                    parametersOf(null, businessId, staffId)
                                }
                                ProductFormScreen(
                                    viewModel = productFormViewModel,
                                    onNavigateBack = { NavigationViewModel.businessNavController?.popBackStack() },
                                    title = title
                                )
                            } // ... error handling
                        }

// Add Route.ProductDetailRoute
                        composable<Route.ProductDetailRoute> {
                            val args = it.toRoute<Route.ProductDetailRoute>()
                            val businessId = userState.currentUser?.businessId
                            val staffId = userState.currentUser?.staffNo

                            if (businessId != null && staffId != null) {
                                val productFormViewModel: ProductFormViewModel = koinViewModel {
                                    // PASS productId FIRST (index 0)
                                    parametersOf(args.productId, businessId, staffId)
                                }
                                ProductFormScreen(
                                    viewModel = productFormViewModel,
                                    onNavigateBack = { NavigationViewModel.businessNavController?.popBackStack() },
                                    title = title
                                )
                            } // ... error handling
                        }
                            composable<Route.StockTransferRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                com.ikemba.inventrar.inventory.presentation.StockTransferScreen(
                                    onTransfer = { /* Process transfer */ },
                                    onCancel = { NavigationViewModel.businessNavController?.popBackStack() }
                                )
                            }

                            composable<Route.AuditTrailRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                com.ikemba.inventrar.audit.presentation.AuditTrailScreen()
                            }
                            composable<Route.DepartmentListRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                // Assumes departmentViewModel is hoisted and passed in
                                DepartmentListScreen(
                                    viewModel = departmentViewModel,
                                    onNavigateToCreateEdit = {
                                        navController.navigate(Route.CreateEditDepartmentRoute)
                                    },
                                    title = title
                                )
                            }

                            composable<Route.CreateEditDepartmentRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                // Assumes departmentViewModel is hoisted and passed in
                                CreateEditDepartmentScreen(
                                    viewModel = departmentViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    title = title
                                )
                            }
                            composable<Route.AddItemToInventoryRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                // ShrineProfileApp(userViewModel = userViewModel, businessViewModel = businessViewModel, dropDownSettingsViewModel = dropDownSettingsViewModel)
                                CreateInventoryForm(
                                    adminViewModel = adminViewModel,
                                    userViewModel = userViewModel
                                )
                            }

                            composable<Route.RoleManagementRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {

                                RoleManagementScreen(
                                    viewModel = rolePermissionViewModel,
                                    onNavigateToCreateRole = {
                                        rolePermissionViewModel.startCreatingRole()
                                        navController.navigate(Route.CreateEditRoleRoute)
                                    },
                                    onNavigateToEditRole = { role ->
                                        // Store the role ID for editing
                                        rolePermissionViewModel.startEditingRole(role)
                                        navController.navigate(Route.CreateEditRoleRoute)
                                    },
                                    title = title
                                )
                            }
                            composable<Route.AddStaffRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                val addStaffViewModel: AddStaffViewModel = koinViewModel(
                                    parameters = { parametersOf(userState.currentUser?.businessId) }
                                )
                                AddStaffScreen(
                                    viewModel = addStaffViewModel, title
                                )
                            }


                            composable<Route.CreateEditRoleRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {

                                CreateEditRoleScreen(
                                    viewModel = rolePermissionViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    title = title
                                )
                            }


                            composable<Route.StaffListRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                val staffListViewModel: com.ikemba.inventrar.admin.presentation.staff.StaffListViewModel =
                                    koinViewModel(
                                        parameters = { parametersOf(userState.currentUser?.businessId) }
                                    )
                                StaffListScreen(
                                    onNavigateToAddStaff = { navController.navigate(Route.AddStaffRoute) },
                                    onNavigateToDetails = { id ->
                                        navController.navigate(
                                            Route.StaffDetailRoute(
                                                id
                                            )
                                        )
                                    },
                                    viewModel = staffListViewModel,
                                    title = title
                                )
                            }

                            composable<Route.StaffDetailRoute> {
                                val args = it.toRoute<Route.StaffDetailRoute>()
                                val vm: com.ikemba.inventrar.admin.presentation.staff.StaffDetailsViewModel =
                                    koinViewModel(
                                        parameters = { parametersOf(args.staffId) }
                                    )
                                StaffDetailsScreen(viewModel = vm, title = title)
                            }


                        }

                    }
                }
            }
        }
        ConfirmLogout(userViewModel)


    }


