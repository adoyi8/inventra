package com.sundroid.kobokobo.app


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
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.Divider
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.sundroid.kobokobo.admin.TwoStepMenuScreen
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.admin.presentation.AdminViewModel
import com.sundroid.kobokobo.admin.presentation.CreateInventoryForm
import com.sundroid.kobokobo.admin.presentation.branch.BranchManagementScreen
import com.sundroid.kobokobo.admin.presentation.rolePermission.CreateEditRoleScreen
import com.sundroid.kobokobo.admin.presentation.rolePermission.RoleManagementScreen
import com.sundroid.kobokobo.admin.presentation.rolePermission.RolePermissionViewModel
import com.sundroid.kobokobo.admin.presentation.staff.StaffDetailsScreen
import com.sundroid.kobokobo.admin.presentation.staff.StaffDetailsViewModel
import com.sundroid.kobokobo.admin.presentation.staff.StaffListScreen
import com.sundroid.kobokobo.admin.presentation.staff.StaffListViewModel
import com.sundroid.kobokobo.audit.presentation.AuditTrailScreen
import com.sundroid.kobokobo.audit.presentation.AuditTrailViewModel
import com.sundroid.kobokobo.auth.GoogleAuthUiProvider
import com.sundroid.kobokobo.business.presentation.AddStaffViewModel
import com.sundroid.kobokobo.business.presentation.BusinessListViewModel
import com.sundroid.kobokobo.business.presentation.BusinessViewModel
import com.sundroid.kobokobo.business.presentation.PaymentMethodsScreen
import com.sundroid.kobokobo.business.presentation.PaymentMethodsViewModel
import com.sundroid.kobokobo.business.presentation.addStaff.AddStaffScreen
import com.sundroid.kobokobo.cart.presentation.HeldCartsScreen
import com.sundroid.kobokobo.core.presentation.NavigationViewModel
import com.sundroid.kobokobo.core.presentation.components.CustomText
import com.sundroid.kobokobo.damages.presentation.DamagesScreen
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.dashboard.presentation.SalesMainScreen
import com.sundroid.kobokobo.dashboard.presentation.components.ConfirmLogout
import com.sundroid.kobokobo.dashboard.utils.Util.APP_NAME
import com.sundroid.kobokobo.department.CreateEditDepartmentScreen
import com.sundroid.kobokobo.department.DepartmentListScreen
import com.sundroid.kobokobo.department.DepartmentViewModel
import com.sundroid.kobokobo.dropdowndata.presentation.DropDownSettingsViewModel
import com.sundroid.kobokobo.expense.presentation.ExpenseScreen
import com.sundroid.kobokobo.expense.presentation.ExpenseViewModel
import com.sundroid.kobokobo.inventory.presentation.CategoryFormScreen
import com.sundroid.kobokobo.inventory.presentation.CategoryFormViewModel
import com.sundroid.kobokobo.inventory.presentation.CategoryListScreen
import com.sundroid.kobokobo.inventory.presentation.CategoryListViewModel
import com.sundroid.kobokobo.inventory.presentation.LocationListScreen
import com.sundroid.kobokobo.inventory.presentation.LocationListViewModel
import com.sundroid.kobokobo.inventory.presentation.ProductFormScreen
import com.sundroid.kobokobo.inventory.presentation.ProductFormViewModel
import com.sundroid.kobokobo.inventory.presentation.ProductListScreen
import com.sundroid.kobokobo.inventory.presentation.ProductListViewModel
import com.sundroid.kobokobo.inventory.presentation.StockTransferScreen
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.services.presentation.ServicesMaterialListScreen
import com.sundroid.kobokobo.services.presentation.ServicesMaterialViewModel
import com.sundroid.kobokobo.services.presentation.ServicesPOSScreen
import com.sundroid.kobokobo.services.presentation.ServicesPOSViewModel
import com.sundroid.kobokobo.services.presentation.history.ServicesTransactionHistoryScreen
import com.sundroid.kobokobo.services.presentation.history.ServicesTransactionHistoryViewModel
import com.sundroid.kobokobo.theme.AppTheme
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionHistoryViewModel
import com.sundroid.kobokobo.user.presentation.ProfileScreen
import inventrar.composeapp.generated.resources.Res
import inventrar.composeapp.generated.resources.admin_panel
import inventrar.composeapp.generated.resources.shopping_basket
import kotlinx.coroutines.launch
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.viewmodel.koinViewModel
import org.koin.core.parameter.parametersOf

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BusinessNavigationGraph(dashboardViewModel: DashboardViewModel = koinViewModel<DashboardViewModel>(), userViewModel: UserViewModel, businessViewModel: BusinessViewModel = koinViewModel(), dropDownSettingsViewModel: DropDownSettingsViewModel = koinViewModel(), adminViewModel: AdminViewModel = koinViewModel(), googleAuthUiProvider: GoogleAuthUiProvider, businessListViewModel: BusinessListViewModel = koinViewModel(), departmentViewModel: DepartmentViewModel = koinViewModel()) {
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val userState by userViewModel.state.collectAsState()
    val navController = rememberNavController()
    val rolePermissionViewModel: RolePermissionViewModel = koinViewModel()
    val title = remember { mutableStateOf(APP_NAME) }
    var parentDestination : MutableState<Route> = remember {mutableStateOf(Route.BusinessListScreenRoute)}

    val currentRoute = navController.currentBackStackEntryFlow
        .collectAsState(initial = null).value?.destination?.route
    LaunchedEffect(true){
        NavigationViewModel.businessNavController = navController
        rolePermissionViewModel.setBusinessId(userState.currentUser?.businessId)
        dashboardViewModel?.setBusinessId(userState.currentUser?.businessId)
        println("togo 3 "+ userState.currentUser?.businessFeatures?.size)

    }

    val features = userState.currentUser?.businessFeatures ?: emptyList()
    val hasSalesFeature = features.isEmpty() || features.contains("Sales")
    val hasLaundryFeature = features.isEmpty() || features.contains("Laundry")
    val hasServicesFeature = features.isEmpty() || features.contains("Services")
    val hasHotelFeature = features.isEmpty() || features.contains("Hotels & Lodgings")

    println("togo 1"+ hasLaundryFeature)
    println("togo 2"+ hasSalesFeature)

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
                            style = 35.sp
                        )
                        Spacer(modifier = Modifier.height(24.dp))
                        if (userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id)) {
                            NavigationDrawerItem(
                                label = { CustomText("Admin Panel") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Route.AdminMenuRoute)
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent
                                )
                            )
                        }
                        if (userState.permissions.contains(SystemPermissions.PROCESS_SALES.id) && hasSalesFeature) {
                            NavigationDrawerItem(
                                label = { CustomText("Sales") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Route.POSScreen)
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent
                                )
                            )
                        }
                        if (userState.permissions.contains(SystemPermissions.PROCESS_LAUNDRY_SALES.id) && hasLaundryFeature) {
                            NavigationDrawerItem(
                                label = { CustomText("Laundry") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Route.LaundryPOSRoute)
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent
                                )
                            )
                        }
                        
                        if (userState.permissions.contains(SystemPermissions.PROCESS_SERVICES_SALES.id) && hasServicesFeature) {
                            NavigationDrawerItem(
                                label = { CustomText("Services") },
                                selected = false,
                                onClick = {
                                    scope.launch {
                                        drawerState.close()
                                        navController.navigate(Route.ServicesPOSRoute)
                                    }
                                },
                                colors = NavigationDrawerItemDefaults.colors(
                                    unselectedContainerColor = Color.Transparent
                                )
                            )
                        }

                            if (userState.permissions.contains(SystemPermissions.ADD_EXPENSES.id)) {
                                NavigationDrawerItem(
                                    label = { CustomText("Expenses") },
                                    selected = false,
                                    onClick = {
                                        scope.launch {
                                            drawerState.close()
                                            navController.navigate(Route.ExpensesScreen)
                                        }
                                    },
                                    colors = NavigationDrawerItemDefaults.colors(
                                        unselectedContainerColor = Color.Transparent
                                    )
                                )
                            }

                         if((userState.permissions.contains(SystemPermissions.ADD_DAMAGES.id)  || userState.permissions.contains(SystemPermissions.VIEW_DAMAGES.id)) && hasSalesFeature) {
                        NavigationDrawerItem(
                            label = { CustomText("Damages") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Route.DamagesScreenRoute)
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            )
                        )
                    }
                        if(userState.permissions.contains(SystemPermissions.PROCESS_SALES.id) && hasSalesFeature) {

                            NavigationDrawerItem(
                            label = { CustomText("Held Orders") },
                            selected = false,
                            onClick = {
                                scope.launch {
                                    drawerState.close()
                                    navController.navigate(Route.HeldCartsRoute)
                                }
                            },
                            colors = NavigationDrawerItemDefaults.colors(
                                unselectedContainerColor = Color.Transparent
                            )
                        )
                        }

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
                            com.sundroid.kobokobo.utils.ShareAppButton(
                                textToShare = "Download Inventra App from here! https://github.com/adoyi8/inventra/releases/download/v1.0/composeApp-release.apk"
                            )
                        },
                    )
                },
                // M3: BottomNavigation is now NavigationBar
                bottomBar = {
                    NavigationBar() {
                        // M3: BottomNavigationItem is now NavigationBarItem
                        if(userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id)){
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    painterResource(Res.drawable.admin_panel),
                                    contentDescription = "Admin Panel",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                            label = { CustomText("Admin Panel") },
                            selected = parentDestination.value == Route.AdminMenuRoute,
                            onClick = { navController.navigate(Route.AdminMenuRoute) }
                        )}
                        if(userState.permissions.contains(SystemPermissions.PROCESS_SALES.id) && hasSalesFeature) {

                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.shopping_basket),
                                        contentDescription = "Sales",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { CustomText("Sales") },
                                selected = parentDestination.value == Route.POSScreen,
                                onClick = { navController.navigate(Route.POSScreen) }
                            )
                        }

                        if(userState.permissions.contains(SystemPermissions.PROCESS_LAUNDRY_SALES.id) && hasLaundryFeature) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.shopping_basket),
                                        contentDescription = "Laundry",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { CustomText("Laundry") },
                                selected = parentDestination.value == Route.LaundryPOSRoute,
                                onClick = { navController.navigate(Route.LaundryPOSRoute) }
                            )
                        }

                        if(userState.permissions.contains(SystemPermissions.PROCESS_SERVICES_SALES.id) && hasServicesFeature) {
                            NavigationBarItem(
                                icon = {
                                    Icon(
                                        painterResource(Res.drawable.shopping_basket),
                                        contentDescription = "Services",
                                        modifier = Modifier.size(24.dp)
                                    )
                                },
                                label = { CustomText("Services") },
                                selected = parentDestination.value == Route.ServicesPOSRoute,
                                onClick = { navController.navigate(Route.ServicesPOSRoute) }
                            )
                        }

                    // TODO: Add permission check for Hotel (e.g., RECEPTIONIST or SYSTEM_ADMIN)
                     // For now, let's assume if they can see POS, or maybe check for 'view_rooms' permission?
                     // Or just check if RECEPTIONIST role is present? 
                     // SystemPermissions don't seem to have specific HOTEL ones yet. 
                     // Let's assume everyone can see it for now or check 'view_sales' as temporary measure or 'view_customrs'?
                     // Just adding it unconditionally for users with a Business for now, or use a general permission.
                        if(userState.permissions.contains(SystemPermissions.ADD_HOTEL_BOOKINGS.id) && hasHotelFeature) {
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = Icons.Default.Home,
                                    contentDescription = "Hotel",
                                    modifier = Modifier.size(24.dp)
                                )
                            },
                        label = { CustomText("Hotel") },
                            selected = parentDestination.value == Route.HotelRoute,
                            onClick = { navController.navigate(Route.HotelRoute) }
                        )
                }
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
                        startDestination = if(userState.permissions.contains(SystemPermissions.PROCESS_SALES.id) && hasSalesFeature) Route.POSScreen else if(userState.permissions.contains(SystemPermissions.ADD_HOTEL_BOOKINGS.id) && hasHotelFeature) Route.HotelRoute else Route.UserProfileRoute

                    ){

                        composable<Route.POSScreen>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Sales"
                            parentDestination.value = Route.POSScreen
                            
                            val transactionHistoryViewModel: TransactionHistoryViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            
                            SalesMainScreen(
                                dashboardViewModel = dashboardViewModel,
                                transactionHistoryViewModel = transactionHistoryViewModel,
                                userViewModel = userViewModel
                            )
                        }
                        composable<Route.AuditTrailRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Audit Trail"
                            AuditTrailScreen(viewModel = koinViewModel<AuditTrailViewModel>(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            ))
                        }
                        composable<Route.HeldCartsRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Held Carts"
                            HeldCartsScreen(
                                viewModel = dashboardViewModel, // Use the same shared ViewModel
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }
                        composable<Route.ExpensesScreen>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Expenses"
                            parentDestination.value = Route.ExpensesScreen
                            ExpenseScreen(
                                viewModel = koinViewModel<ExpenseViewModel>(
                                parameters = {
                                    parametersOf(
                                        userState.currentUser?.businessId,
                                    )
                                }
                            ),
                                staffId = userState.currentUser?.staffNo?:"",
                                userViewModel = userViewModel)
                        }

                        composable<Route.UserProfileRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Profile"
                            parentDestination.value = Route.UserProfileRoute
                            ProfileScreen()
                        }
                        composable<Route.AdminMenuRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Admin Panel"
                            parentDestination.value = Route.AdminMenuRoute
                            TwoStepMenuScreen()
                        }
                        composable<Route.PaymentMethodsRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            val viewModel: PaymentMethodsViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            title.value = "Payment Methods"
                            PaymentMethodsScreen(viewModel)
                        }
                        composable<Route.BranchManagementRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Branch"
                            BranchManagementScreen()
                        }
                        composable<Route.ProductListRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {

                            val productListViewModel: ProductListViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            title.value = "Products"
                            ProductListScreen(
                                viewModel = productListViewModel,
                                onNavigateToAddProduct = {
                                    // Navigate to the combined form screen for Add (productId = null)
                                    NavigationViewModel.businessNavController?.navigate(Route.AddProductRoute)
                                },
                                onNavigateToProductDetail = { productId ->
                                    // Navigate to the combined form screen for Detail/Edit
                                    NavigationViewModel.businessNavController?.navigate(Route.ProductDetailRoute(productId))
                                },
                                onNavigateToProductHistory = { productId ->
                                    NavigationViewModel.businessNavController?.navigate(Route.ProductAuditTrailRoute(productId))
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
                                LocationListScreen(
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
                                    title = title,
                                    onNavigateToAddCategory = { NavigationViewModel.businessNavController?.navigate(Route.AddCategory) }
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
                                com.sundroid.kobokobo.inventory.presentation.ProductDetailScreen(
                                    viewModel = productFormViewModel,
                                    onNavigateBack = { NavigationViewModel.businessNavController?.popBackStack() },
                                    onNavigateToEditProduct = { NavigationViewModel.businessNavController?.navigate(Route.EditProductRoute(args.productId)) },
                                    hasEditPermission = userState.permissions.contains(SystemPermissions.SYSTEM_ADMIN.id),
                                    title = title
                                )
                            } // ... error handling
                        }

                        composable<Route.EditProductRoute> {
                            val args = it.toRoute<Route.EditProductRoute>()
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
                                    title = title,
                                    onNavigateToAddCategory = { NavigationViewModel.businessNavController?.navigate(Route.AddCategory) }
                                )
                            } // ... error handling
                        }

                        composable<Route.ProductAuditTrailRoute> {
                            val args = it.toRoute<Route.ProductAuditTrailRoute>()
                            val businessId = userState.currentUser?.businessId
                            val viewModel: AuditTrailViewModel = koinViewModel { parametersOf(businessId) }
                            
                            com.sundroid.kobokobo.inventory.presentation.ProductAuditTrailScreen(
                                productId = args.productId,
                                onNavigateBack = { NavigationViewModel.businessNavController?.popBackStack() },
                                viewModel = viewModel
                            )
                        }
                            composable<Route.StockTransferRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                StockTransferScreen(
                                    onTransfer = { /* Process transfer */ },
                                    onCancel = { NavigationViewModel.businessNavController?.popBackStack() }
                                )
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
                                    userViewModel = userViewModel,
                                    onNavigateBack = { navController.popBackStack() },
                                    title = title
                                )
                            }


                            composable<Route.StaffListRoute>(
                                exitTransition = { slideOutHorizontally() },
                                popEnterTransition = { slideInHorizontally() }
                            ) {
                                val staffListViewModel: StaffListViewModel =
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
                                val vm: StaffDetailsViewModel =
                                    koinViewModel(
                                        parameters = { parametersOf(args.staffId, userState.currentUser?.businessId) }
                                    )
                                StaffDetailsScreen(viewModel = vm, title = title)
                            }
                        composable<Route.DamagesScreenRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Damages"
                            parentDestination.value = Route.DamagesScreenRoute

                            // 1. Get businessId and staffId/name from userState
                            val businessId = userState.currentUser?.businessId ?: ""
                            val staffId = userState.currentUser?.staffNo ?: ""
                            val staffName = userState.currentUser?.getFullName() ?: ""

                            // 2. Render the DamagesScreen with its ViewModel
                            DamagesScreen(
                                onNavigateBack = {
                                    // For the main screen, back could go to the start destination or handle back press
                                    navController.popBackStack(Route.POSScreen, false)
                                },
                                businessId = businessId,
                                staffId = staffId,
                                staffName = staffName
                                // ViewModel is provided by Koin via koinViewModel() in the screen itself
                            )
                        }


                        // --- NEW: Add Damage Screen Composable ---



                        composable<Route.HotelRoute>(
                            exitTransition = { slideOutHorizontally() },
                             popEnterTransition = { slideInHorizontally() }
                        ) {
                             title.value = "Hotel Management"
                             parentDestination.value = Route.HotelRoute
                             
                             val businessId = userState.currentUser?.businessId
                             val staffId = userState.currentUser?.id // or staffNo depending on what HotelMainScreen expects. 
                             // HotelViewModel expects 'staffId' (String). User.id is usually the firebase uid. 
                             // Let's use userState.currentUser.id
                             
                             if (businessId != null && staffId != null) {
                                  com.sundroid.kobokobo.hotel.presentation.HotelMainScreen(
                                      businessId = businessId,
                                      staffId = staffId,
                                      userViewModel = userViewModel
                                  )
                             }
                        }
                        
                        composable<Route.RoomManagementRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Room Management"
                            val hotelViewModel: com.sundroid.kobokobo.hotel.presentation.HotelViewModel = koinViewModel(
                                 parameters = { parametersOf(userState.currentUser?.businessId, userState.currentUser?.id) }
                            )
                             
                            val hotelState by hotelViewModel.state.collectAsState()
                            
                            com.sundroid.kobokobo.admin.presentation.RoomManagementScreen(
                                viewModel = hotelViewModel,
                                state = hotelState
                            )
                        }

                        composable<Route.LaundryPOSRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Laundry POS"
                            parentDestination.value = Route.LaundryPOSRoute
                            
                            val laundryViewModel: com.sundroid.kobokobo.laundry.presentation.LaundryMaterialViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            
                            val posViewModel: com.sundroid.kobokobo.laundry.presentation.LaundryPOSViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId, userState.currentUser?.staffNo, userState.currentUser?.getFullName()) }
                            )
                            
                            com.sundroid.kobokobo.laundry.presentation.LaundryPOSScreen(
                                laundryViewModel = laundryViewModel,
                                posViewModel = posViewModel,
                                userViewModel = userViewModel,
                                onNavigateToHistory = { navController.navigate(Route.LaundryTransactionHistoryRoute) }
                            )
                        }

                        composable<Route.LaundryMaterialListRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Laundry Materials"
                            parentDestination.value = Route.LaundryMaterialListRoute
                            
                            val laundryViewModel: com.sundroid.kobokobo.laundry.presentation.LaundryMaterialViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            
                            com.sundroid.kobokobo.laundry.presentation.LaundryMaterialListScreen(
                                viewModel = laundryViewModel,
                                staffId = userState.currentUser?.staffNo ?: "",
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.LaundryTransactionHistoryRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Laundry History"
                            parentDestination.value = Route.LaundryTransactionHistoryRoute
                            
                            val historyViewModel: com.sundroid.kobokobo.laundry.presentation.history.LaundryTransactionHistoryViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            
                            com.sundroid.kobokobo.laundry.presentation.history.LaundryTransactionHistoryScreen(
                                viewModel = historyViewModel,
                                userViewModel = userViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.ServicesPOSRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Services POS"
                            parentDestination.value = Route.ServicesPOSRoute

                            val ServicesViewModel: ServicesMaterialViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )
                            val posViewModel: ServicesPOSViewModel = koinViewModel(
                                parameters = {
                                    parametersOf(
                                        userState.currentUser?.businessId,
                                        userState.currentUser?.staffNo,
                                        userState.currentUser?.getFullName()
                                    )
                                }
                            )

                            ServicesPOSScreen(
                                ServicesViewModel = ServicesViewModel,
                                posViewModel = posViewModel,
                                userViewModel = userViewModel,
                                onNavigateToHistory = { navController.navigate(Route.ServicesTransactionHistoryRoute) }
                            )
                        }

                        composable<Route.ServicesMaterialListRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Services Prices"
                            parentDestination.value = Route.ServicesMaterialListRoute

                            val ServicesViewModel: ServicesMaterialViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )

                            ServicesMaterialListScreen(
                                viewModel = ServicesViewModel,
                                staffId = userState.currentUser?.staffNo ?: "",
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        composable<Route.ServicesTransactionHistoryRoute>(
                            exitTransition = { slideOutHorizontally() },
                            popEnterTransition = { slideInHorizontally() }
                        ) {
                            title.value = "Services History"
                            parentDestination.value = Route.ServicesTransactionHistoryRoute

                            val historyViewModel: ServicesTransactionHistoryViewModel = koinViewModel(
                                parameters = { parametersOf(userState.currentUser?.businessId) }
                            )

                            ServicesTransactionHistoryScreen(
                                viewModel = historyViewModel,
                                userViewModel = userViewModel,
                                onNavigateBack = { navController.popBackStack() }
                            )
                        }

                        }


                    }
                }
            }
        }
        ConfirmLogout(userViewModel)
    }


