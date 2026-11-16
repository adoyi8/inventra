package com.sundroid.kobokobo.di


import androidx.sqlite.driver.bundled.BundledSQLiteDriver
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.sundroid.kobokobo.services.presentation.ServicesPOSViewModel
import com.sundroid.kobokobo.admin.data.domain.repository.BranchRepository
import com.sundroid.kobokobo.admin.data.domain.repository.DepartmentRepository
import com.sundroid.kobokobo.admin.data.domain.repository.RolePermissionRepository
import com.sundroid.kobokobo.admin.data.domain.useCase.AssignPermissionsToRoleUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.AssignRoleToUserUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.CreateBranchUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.CreateDepartmentUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.CreateRoleUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.DeleteBranchUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.DeleteDepartmentUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.DeleteRoleUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetAllPermissionsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetAllRolesUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetBranchByIdUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetBranchesUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetDepartmentByIdUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetDepartmentsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetPermissionsByCategoryUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetRoleByIdUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetRolePermissionsList
import com.sundroid.kobokobo.admin.data.domain.useCase.GetRolePermissionsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetUserPermissionsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.GetUserRolesUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.InitializeBusinessRolesUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.InitializeSystemPermissionsUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.RemovePermissionsFromRoleUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.RemoveRoleFromUserUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.UpdateBranchUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.UpdateDepartmentUseCase
import com.sundroid.kobokobo.admin.data.domain.useCase.UpdateRoleUseCase
import com.sundroid.kobokobo.admin.data.repository.DefaultBranchRepository
import com.sundroid.kobokobo.admin.data.repository.DefaultDepartmentRepository
import com.sundroid.kobokobo.admin.data.repository.DefaultRolePermissionRepository
import com.sundroid.kobokobo.admin.presentation.AdminViewModel
import com.sundroid.kobokobo.admin.presentation.rolePermission.RolePermissionViewModel
import com.sundroid.kobokobo.admin.presentation.staff.StaffDetailsViewModel
import com.sundroid.kobokobo.admin.presentation.staff.StaffListViewModel
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.audit.data.domain.useCase.CreateAuditTrailUseCase
import com.sundroid.kobokobo.audit.data.domain.useCase.GetAuditTrailsByProductUseCase
import com.sundroid.kobokobo.audit.data.domain.useCase.GetAuditTrailsByStaffUseCase
import com.sundroid.kobokobo.audit.data.domain.useCase.GetAuditTrailsByTransactionUseCase
import com.sundroid.kobokobo.audit.data.domain.useCase.GetAuditTrailsUseCase
import com.sundroid.kobokobo.audit.data.repository.DefaultAuditTrailRepository
import com.sundroid.kobokobo.audit.presentation.AuditTrailViewModel
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.useCase.AddStaffUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.CreateBusinessUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.FindUserByEmailUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.GetBusinessesForUserUseCase
import com.sundroid.kobokobo.business.data.domain.useCase.GetStaffRoleUseCase
import com.sundroid.kobokobo.business.data.repository.DefaultBusinessRepository
import com.sundroid.kobokobo.business.presentation.AddStaffViewModel
import com.sundroid.kobokobo.business.presentation.BusinessListViewModel
import com.sundroid.kobokobo.business.presentation.PaymentMethodsViewModel
import com.sundroid.kobokobo.cart.data.repository.DefaultCartRepository
import com.sundroid.kobokobo.cart.domain.CartRepository
import com.sundroid.kobokobo.damages.data.domain.CreateDamageUseCase
import com.sundroid.kobokobo.damages.data.domain.DamageRepository
import com.sundroid.kobokobo.damages.data.domain.GetDamagesUseCase
import com.sundroid.kobokobo.damages.data.repository.DefaultDamageRepository
import com.sundroid.kobokobo.damages.presentation.DamagesViewModel
import com.sundroid.kobokobo.dashboard.data.database.ProductDatabase
import com.sundroid.kobokobo.dashboard.data.database.ProductDatabaseFactory
import com.sundroid.kobokobo.dashboard.data.repository.DefaultProductRepository
import com.sundroid.kobokobo.dashboard.domain.ProductRepository
import com.sundroid.kobokobo.dashboard.presentation.DashboardViewModel
import com.sundroid.kobokobo.department.DepartmentViewModel
import com.sundroid.kobokobo.expense.data.repository.DefaultExpenseRepository
import com.sundroid.kobokobo.expense.domain.CreateExpenseUseCase
import com.sundroid.kobokobo.expense.domain.ExpenseRepository
import com.sundroid.kobokobo.expense.domain.GetExpenseCategoriesUseCase
import com.sundroid.kobokobo.expense.domain.GetExpensesUseCase
import com.sundroid.kobokobo.expense.presentation.ExpenseViewModel
import com.sundroid.kobokobo.inventory.data.domain.repository.InventoryRepository
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateLocationUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.CreateStockMovementUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteLocationUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.DeleteProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoriesUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetCategoryByIdUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetLocationByIdUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetLocationsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductByIdUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductsByCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetProductsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetStockLevelUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetStockLevelsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.GetStockMovementsUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateCategoryUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateLocationUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateProductUseCase
import com.sundroid.kobokobo.inventory.data.domain.useCase.UpdateStockLevelUseCase
import com.sundroid.kobokobo.inventory.data.repository.DefaultInventoryRepository
import com.sundroid.kobokobo.inventory.presentation.AddCategoryViewModel
import com.sundroid.kobokobo.inventory.presentation.AddLocationViewModel
import com.sundroid.kobokobo.inventory.presentation.AddProductViewModel
import com.sundroid.kobokobo.inventory.presentation.CategoryFormViewModel
import com.sundroid.kobokobo.inventory.presentation.CategoryListViewModel
import com.sundroid.kobokobo.inventory.presentation.LocationListViewModel
import com.sundroid.kobokobo.inventory.presentation.ProductFormViewModel
import com.sundroid.kobokobo.inventory.presentation.ProductListViewModel
import com.sundroid.kobokobo.inventory.presentation.StockTransferViewModel
import com.sundroid.kobokobo.login.presentation.UserViewModel
import com.sundroid.kobokobo.transactionHistory.data.repository.DefaultTransactionHistoryRepository
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistoryRepository
import com.sundroid.kobokobo.transactionHistory.presentation.TransactionHistoryViewModel
import com.sundroid.kobokobo.user.data.database.UserDatabase
import com.sundroid.kobokobo.user.data.database.UserDatabaseFactory
import com.sundroid.kobokobo.user.data.repository.DefaultUserRepository
import com.sundroid.kobokobo.user.domain.UserRepository
import com.sundroid.kobokobo.hotel.data.repository.FirebaseHotelRepository
import com.sundroid.kobokobo.hotel.domain.repository.HotelRepository
import com.sundroid.kobokobo.hotel.presentation.HotelViewModel
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryRepository
import com.sundroid.kobokobo.laundry.data.domain.repository.LaundryTransactionRepository
import com.sundroid.kobokobo.laundry.data.repository.LaundryRepositoryImpl
import com.sundroid.kobokobo.laundry.data.repository.LaundryTransactionRepositoryImpl
import com.sundroid.kobokobo.laundry.presentation.LaundryMaterialViewModel
import com.sundroid.kobokobo.laundry.presentation.LaundryPOSViewModel
import com.sundroid.kobokobo.services.data.domain.repository.ServicesRepository
import com.sundroid.kobokobo.services.data.domain.repository.ServicesTransactionRepository
import com.sundroid.kobokobo.services.data.repository.ServicesRepositoryImpl
import com.sundroid.kobokobo.services.data.repository.ServicesTransactionRepositoryImpl
import com.sundroid.kobokobo.services.presentation.ServicesMaterialViewModel

import org.koin.core.module.Module
import org.koin.core.module.dsl.factoryOf
import org.koin.core.module.dsl.singleOf
import org.koin.core.module.dsl.viewModel
import org.koin.core.module.dsl.viewModelOf
import org.koin.dsl.bind
import org.koin.dsl.module

expect val platformModule: Module

val sharedModule = module {

    singleOf(::DefaultUserRepository).bind<UserRepository>()
    singleOf(::DefaultProductRepository).bind<ProductRepository>()
    // --- ADD EXPENSE REPOSITORY ---
    singleOf(::DefaultExpenseRepository).bind<ExpenseRepository>()
    singleOf(::DefaultCartRepository).bind<CartRepository>()
    singleOf(::DefaultTransactionHistoryRepository).bind<TransactionHistoryRepository>()
    singleOf(::DefaultBusinessRepository).bind<BusinessRepository>()
    singleOf(::DefaultInventoryRepository).bind<InventoryRepository>()

    
    // Laundry Management
    singleOf(::LaundryRepositoryImpl).bind<LaundryRepository>()
    singleOf(::LaundryTransactionRepositoryImpl).bind<LaundryTransactionRepository>()

    // Services Management
    singleOf(::ServicesRepositoryImpl).bind<ServicesRepository>()
    singleOf(::ServicesTransactionRepositoryImpl).bind<ServicesTransactionRepository>()

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


    // --- ADD EXPENSE USE CASES ---
    factoryOf(::GetExpensesUseCase)
    factoryOf(::CreateExpenseUseCase)
    factoryOf(::GetExpenseCategoriesUseCase)

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
    single { get<ProductDatabase>().heldCartDao }

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
    factoryOf(::GetRolePermissionsList)
    factoryOf(::AssignRoleToUserUseCase)
    factoryOf(::RemoveRoleFromUserUseCase)
    factoryOf(::GetUserRolesUseCase)
    factoryOf(::GetUserPermissionsUseCase)
    factoryOf(::InitializeBusinessRolesUseCase)
    factoryOf(::InitializeSystemPermissionsUseCase)




    viewModelOf(::UserViewModel)
    viewModelOf(::DashboardViewModel)
    viewModelOf(::TransactionHistoryViewModel)
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
            rolePermissionRepository = get(),
            staffId = params.get(),
            businessId = params.get()
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
        ExpenseViewModel(
            getExpensesUseCase = get(),
            createExpenseUseCase = get(),
            getExpenseCategoriesUseCase = get(),
            businessRepository = get(),
            businessId = params.get() // Pass businessId like you do for other VMs
        )
    }

    viewModel { params ->
        AuditTrailViewModel(
            businessId = params.get(),
            auditTrailRepository = get(),
            businessRepository = get()
        )
    }

    singleOf(::DefaultDamageRepository).bind<DamageRepository>()
    factoryOf(::CreateDamageUseCase)
    factoryOf(::GetDamagesUseCase)
    viewModel { params ->
        DamagesViewModel(
            getDamagesUseCase = get(),
            createDamageUseCase = get(),
            getProductsUseCase = get(), // Make sure this exists and takes businessId
            businessId = params.get(),
            staffId = params.get(),
            staffName = params.get()
        )
    }

    // Hotel Module
    singleOf(::FirebaseHotelRepository).bind<HotelRepository>()
    viewModel { params ->
        HotelViewModel(
            hotelRepository = get(),
            businessRepository = get(),
            businessId = params.get(),
            staffId = params.get()
        )
    }

    viewModel { params ->
        LaundryMaterialViewModel(
            laundryRepository = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        LaundryPOSViewModel(
            transactionRepository = get(),
            businessId = params.get(),
            staffId = params.get(),
            staffName = params.get()
        )
    }

    viewModel { params ->
        PaymentMethodsViewModel(
            businessRepository = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        com.sundroid.kobokobo.laundry.presentation.history.LaundryTransactionHistoryViewModel(
            repository = get(),
            businessRepository = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        ServicesMaterialViewModel(
            servicesRepository = get(),
            businessId = params.get()
        )
    }

    viewModel { params ->
        ServicesPOSViewModel(
            transactionRepository = get(),
            businessRepository = get(),
            businessId = params.get(),
            staffId = params.get(),
            staffName = params.get()
        )
    }

    viewModel { params ->
        com.sundroid.kobokobo.services.presentation.history.ServicesTransactionHistoryViewModel(
            repository = get(),
            businessRepository = get(),
            businessId = params.get()
        )
    }
}