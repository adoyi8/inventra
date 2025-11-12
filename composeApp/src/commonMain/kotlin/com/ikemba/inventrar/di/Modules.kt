package com.ikemba.inventrar.di


import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.ikemba.inventrar.admin.presentation.AdminViewModel
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.domain.useCase.AddStaffUseCase
import com.ikemba.inventrar.business.data.domain.useCase.CreateBusinessUseCase
import com.ikemba.inventrar.business.data.domain.useCase.FindUserByEmailUseCase
import com.ikemba.inventrar.business.data.domain.useCase.GetBusinessesForUserUseCase
import com.ikemba.inventrar.business.data.domain.useCase.GetStaffRoleUseCase
import com.ikemba.inventrar.business.network.KtorRemoteBusinessDataSource
import com.ikemba.inventrar.business.network.RemoteBusinessDataSource
import com.ikemba.inventrar.cart.data.network.KtorRemoteCartDataSource
import com.ikemba.inventrar.cart.data.network.RemoteCartDataSource
import com.ikemba.inventrar.cart.data.repository.DefaultCartRepository
import com.ikemba.inventrar.cart.domain.CartRepository
import com.ikemba.inventrar.core.data.HttpClientFactory
import com.ikemba.inventrar.dashboard.data.database.ProductDatabase
import com.ikemba.inventrar.dashboard.data.database.ProductDatabaseFactory
import com.ikemba.inventrar.dashboard.data.network.KtorRemoteProductDataSource
import com.ikemba.inventrar.dashboard.data.network.RemoteProductDataSource
import com.ikemba.inventrar.dashboard.data.repository.DefaultProductRepository
import com.ikemba.inventrar.dashboard.domain.ProductRepository
import com.ikemba.inventrar.dashboard.presentation.DashboardViewModel
import com.ikemba.inventrar.heldOrder.data.domain.HeldOrderRepository
import com.ikemba.inventrar.heldOrder.data.network.KtorRemoteHeldOrderDataSource
import com.ikemba.inventrar.heldOrder.data.network.RemoteHeldOrderDataSource
import com.ikemba.inventrar.heldOrder.data.repository.DefaultHeldOrderRepository
import com.ikemba.inventrar.heldOrder.presentation.HeldOrderViewModel
import com.ikemba.inventrar.login.presentation.UserViewModel
import com.ikemba.inventrar.splashScreen.presentation.SplashScreenViewModel
import com.ikemba.inventrar.transactionHistory.data.network.KtorRemoteTransactionHistoryDataSource
import com.ikemba.inventrar.transactionHistory.data.network.RemoteTransactionHistoryDataSource
import com.ikemba.inventrar.transactionHistory.data.repository.DefaultTransactionHistoryRepository
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistoryRepository
import com.ikemba.inventrar.transactionHistory.presentation.TransactionHistoryViewModel
import com.ikemba.inventrar.user.data.database.UserDatabase
import com.ikemba.inventrar.user.data.database.UserDatabaseFactory
import com.ikemba.inventrar.user.data.network.KtorRemoteUserDataSource
import com.ikemba.inventrar.user.data.network.RemoteUserDataSource
import com.ikemba.inventrar.user.data.repository.DefaultUserRepository
import com.ikemba.inventrar.user.domain.UserRepository
import com.ikemba.inventrar.changePassword.presentation.ChangePasswordViewModel
import org.koin.core.module.Module
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

import com.ikemba.inventrar.business.data.repository.DefaultBusinessRepository
import com.ikemba.inventrar.business.presentation.AddStaffViewModel
import com.ikemba.inventrar.business.presentation.BusinessListViewModel
import com.ikemba.inventrar.business.presentation.BusinessViewModel
import com.ikemba.inventrar.dropdowndata.data.domain.DropDownSettingsRepository
import com.ikemba.inventrar.dropdowndata.data.repository.DefaultDropDownSettingsRepository
import com.ikemba.inventrar.dropdowndata.network.KtorRemoteDropDownSettingsDataSource
import com.ikemba.inventrar.dropdowndata.network.RemoteDropDownSettingsDataSource
import com.ikemba.inventrar.dropdowndata.presentation.DropDownSettingsViewModel

import dev.gitlive.firebase.Firebase
import org.koin.core.module.dsl.viewModel
import com.ikemba.inventrar.business.data.domain.model.AppUser
import org.koin.core.module.dsl.factoryOf
import com.ikemba.inventrar.admin.data.domain.repository.RolePermissionRepository
import com.ikemba.inventrar.admin.data.repository.DefaultRolePermissionRepository
import com.ikemba.inventrar.admin.data.domain.repository.DepartmentRepository
import com.ikemba.inventrar.admin.data.repository.DefaultDepartmentRepository
import com.ikemba.inventrar.admin.data.domain.useCase.*
import com.ikemba.inventrar.admin.presentation.rolePermission.RolePermissionViewModel
import com.ikemba.inventrar.admin.presentation.staff.StaffListViewModel
import com.ikemba.inventrar.admin.presentation.staff.StaffDetailsViewModel
import com.ikemba.inventrar.admin.data.domain.useCase.CreateDepartmentUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.UpdateDepartmentUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.DeleteDepartmentUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.GetDepartmentByIdUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.GetDepartmentsUseCase
import com.ikemba.inventrar.admin.data.domain.model.Branch
import com.ikemba.inventrar.admin.data.domain.repository.BranchRepository
import com.ikemba.inventrar.admin.data.repository.DefaultBranchRepository
import com.ikemba.inventrar.admin.data.domain.useCase.CreateBranchUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.UpdateBranchUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.DeleteBranchUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.GetBranchByIdUseCase
import com.ikemba.inventrar.admin.data.domain.useCase.GetBranchesUseCase
import com.ikemba.inventrar.inventory.data.domain.repository.InventoryRepository
import com.ikemba.inventrar.inventory.data.repository.DefaultInventoryRepository
import com.ikemba.inventrar.audit.data.domain.repository.AuditTrailRepository
import com.ikemba.inventrar.audit.data.repository.DefaultAuditTrailRepository
import com.ikemba.inventrar.inventory.data.domain.useCase.*
import com.ikemba.inventrar.audit.data.domain.useCase.*
import com.ikemba.inventrar.department.DepartmentViewModel
import com.ikemba.inventrar.inventory.presentation.*
import com.ikemba.inventrar.audit.presentation.AuditTrailViewModel

expect val platformModule: Module

val sharedModule = module {
    single { HttpClientFactory.create(get()) }

    singleOf(::DefaultUserRepository).bind<UserRepository>()
    singleOf(::KtorRemoteUserDataSource).bind<RemoteUserDataSource>()
    singleOf(::DefaultProductRepository).bind<ProductRepository>()
    // RemoteProductDataSource no longer used for products; Firestore used instead
    singleOf(::DefaultCartRepository).bind<CartRepository>()
    // RemoteCartDataSource no longer used for cart; Firestore used instead
    singleOf(::DefaultTransactionHistoryRepository).bind<TransactionHistoryRepository>()
    // RemoteTransactionHistoryDataSource no longer used; Firestore used instead
    singleOf(::DefaultHeldOrderRepository).bind<HeldOrderRepository>()
    singleOf(::KtorRemoteHeldOrderDataSource).bind<RemoteHeldOrderDataSource>()
    singleOf(::DefaultBusinessRepository).bind<BusinessRepository>()
    singleOf(::KtorRemoteBusinessDataSource).bind<RemoteBusinessDataSource>()
    singleOf(::DefaultInventoryRepository).bind<InventoryRepository>()

    singleOf(::DefaultDropDownSettingsRepository).bind<DropDownSettingsRepository>()
    singleOf(::KtorRemoteDropDownSettingsDataSource).bind<RemoteDropDownSettingsDataSource>()

    // Role and Permission Management
    singleOf(::DefaultRolePermissionRepository).bind<RolePermissionRepository>()
    // Department Management
    singleOf(::DefaultDepartmentRepository).bind<DepartmentRepository>()
    // Branch Management
    singleOf(::DefaultBranchRepository).bind<BranchRepository>()
    // Inventory Management
    singleOf(::DefaultInventoryRepository).bind<InventoryRepository>()
    // Audit Trail Management
    singleOf(::DefaultAuditTrailRepository).bind<AuditTrailRepository>()

    // Department use cases
    factoryOf(::CreateDepartmentUseCase)
    factoryOf(::UpdateDepartmentUseCase)
    factoryOf(::DeleteDepartmentUseCase)
    factoryOf(::GetDepartmentByIdUseCase)
    factoryOf(::GetDepartmentsUseCase)
    // Branch use cases
    factoryOf(::CreateBranchUseCase)
    factoryOf(::UpdateBranchUseCase)
    factoryOf(::DeleteBranchUseCase)
    factoryOf(::GetBranchByIdUseCase)
    factoryOf(::GetBranchesUseCase)
    
    // Inventory use cases
    factoryOf(::CreateProductUseCase)
    factoryOf(::UpdateProductUseCase)
    factoryOf(::DeleteProductUseCase)
    factoryOf(::GetProductByIdUseCase)
    factoryOf(::GetProductsUseCase)
    factoryOf(::GetProductsByCategoryUseCase)
    factoryOf(::CreateCategoryUseCase)
    factoryOf(::UpdateCategoryUseCase)
    factoryOf(::DeleteCategoryUseCase)
    factoryOf(::GetCategoryByIdUseCase)
    factoryOf(::GetCategoriesUseCase)
    factoryOf(::CreateLocationUseCase)
    factoryOf(::UpdateLocationUseCase)
    factoryOf(::DeleteLocationUseCase)
    factoryOf(::GetLocationByIdUseCase)
    factoryOf(::GetLocationsUseCase)
    factoryOf(::CreateStockMovementUseCase)
    factoryOf(::GetStockLevelUseCase)
    factoryOf(::UpdateStockLevelUseCase)
    factoryOf(::GetStockLevelsUseCase)
    factoryOf(::GetStockMovementsUseCase)
    
    // Audit Trail use cases
    factoryOf(::CreateAuditTrailUseCase)
    factoryOf(::GetAuditTrailsUseCase)
    factoryOf(::GetAuditTrailsByProductUseCase)
    factoryOf(::GetAuditTrailsByStaffUseCase)
    factoryOf(::GetAuditTrailsByTransactionUseCase)
    
    // Inventory ViewModels




    single {
        get<UserDatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single {
        get<ProductDatabaseFactory>().create()
            .setDriver(BundledSQLiteDriver())
            .fallbackToDestructiveMigration(dropAllTables = true)
            .build()
    }
    single { get<ProductDatabase>().productDao }

    single { get<UserDatabase>().userDao }
    single { get<UserDatabase>().businessDao}
    single { get<ProductDatabase>().cartDao }
    single { get<ProductDatabase>().postSalesRequestDao }
    single { FirebaseAuth.getInstance() }
    single { FirebaseFirestore.getInstance() }


    factoryOf(::AddStaffUseCase)
    factoryOf(::CreateBusinessUseCase)
    factoryOf(::FindUserByEmailUseCase)
    factoryOf(::GetBusinessesForUserUseCase)
    factoryOf(::GetStaffRoleUseCase)

    // Role and Permission Use Cases
    factoryOf(::CreateRoleUseCase)
    factoryOf(::UpdateRoleUseCase)
    factoryOf(::DeleteRoleUseCase)
    factoryOf(::GetAllRolesUseCase)
    factoryOf(::GetRoleByIdUseCase)
    factoryOf(::GetAllPermissionsUseCase)
    factoryOf(::GetPermissionsByCategoryUseCase)
    factoryOf(::AssignPermissionsToRoleUseCase)
    factoryOf(::RemovePermissionsFromRoleUseCase)
    factoryOf(::GetRolePermissionsUseCase)
    factoryOf(::AssignRoleToUserUseCase)
    factoryOf(::RemoveRoleFromUserUseCase)
    factoryOf(::GetUserRolesUseCase)
    factoryOf(::GetUserPermissionsUseCase)
    factoryOf(::InitializeBusinessRolesUseCase)
    factoryOf(::InitializeSystemPermissionsUseCase)




    viewModelOf(::UserViewModel)
    viewModelOf(::SplashScreenViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::TransactionHistoryViewModel)
    viewModelOf(::HeldOrderViewModel)
    viewModelOf(::ChangePasswordViewModel)
    viewModelOf(::BusinessViewModel)
    viewModelOf(::DropDownSettingsViewModel)
    viewModelOf(::AdminViewModel)
    viewModelOf(::BusinessListViewModel)
    viewModelOf(::RolePermissionViewModel)
    viewModelOf(::DepartmentViewModel)
    viewModel { params ->
        StaffListViewModel(
            businessRepository = get(),
            businessId = params.getOrNull()
        )
    }
    viewModel{ params -> ProductListViewModel(
        getProductsUseCase = get(),
        deleteProductUseCase = get(),
        businessId = params.get()
    )}
    viewModel{params ->
        AddProductViewModel(
            createProductUseCase = get(),
            getCategoriesUseCase = get(),
            businessId = params.get(),
            staffId = params.get()
        )
    }
    viewModel { params ->
        StaffDetailsViewModel(
            businessRepository = get(),
            staffId = params.get()
        )
    }

    viewModel { params ->
        AddStaffViewModel(
            findUserByEmailUseCase = get(),
            addStaffUseCase = get(),
            getDepartmentsUseCase = get(), // <-- INJECT
            getAllRolesUseCase = get(),
            currentBusinessId = params.get() // Parameter for the selected business ID
        )
    }
    viewModel { params ->
        CategoryListViewModel(
            getCategoriesUseCase = get(),
            deleteCategoryUseCase = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        AddCategoryViewModel(
            createCategoryUseCase = get(),
            businessId = params.get(),
            staffId = params.get()
        )
    }

    viewModel { params ->
        LocationListViewModel(
            getLocationsUseCase = get(),
            deleteLocationUseCase = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        AddLocationViewModel(
            createLocationUseCase = get(),
            businessId = params.get(),
            staffId = params.get()
        )
    }
// Updated Koin Definition
    viewModel { params ->
        // params.getOrNull<String>() attempts to get the categoryId, which is optional
        //val categoryId: String? = params.getOrNull()

        val categoryId: String? = params.get(0) // Index 0
        val businessId: String = params.get(1)        // Index 1
        val staffId: String = params.get(2)
        CategoryFormViewModel(
            createCategoryUseCase = get(),
            getCategoryByIdUseCase = get(),
            updateCategoryUseCase = get(),
            deleteCategoryUseCase = get(),
            businessId = businessId, // Assumes businessId is always passed
            staffId = staffId,    // Assumes staffId is always passed
            categoryId = categoryId
        )
    }
    viewModel { params ->
        // The parameter order should be: productId?, businessId, staffId
        val productId: String? = params.get(0)

        ProductFormViewModel(
            createProductUseCase = get(),
            updateProductUseCase = get(),
            deleteProductUseCase = get(),
            getProductByIdUseCase = get(),
            getCategoriesUseCase = get(),
            businessId = params.get(1), // Assuming businessId is at index 1
            staffId = params.get(2),    // Assuming staffId is at index 2
            productId = productId
        )
    }
    viewModel { params ->
        StockTransferViewModel(
            createStockMovementUseCase = get(),
            getProductsUseCase = get(),
            getLocationsUseCase = get(),
            businessId = params.get(),
            staffId = params.get()
        )
    }

    viewModel { params ->
        AuditTrailViewModel(
            getAuditTrailsUseCase = get(),
            businessId = params.get()
        )
    }
}