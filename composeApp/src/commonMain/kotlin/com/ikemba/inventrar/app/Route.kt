package com.ikemba.inventrar.app

import kotlinx.serialization.Serializable

sealed interface Route {

    @Serializable
    data object BookGraph: Route

    @Serializable
    data object BookList: Route

    @Serializable
    data object Login: Route

    @Serializable
    data object SplashScreen: Route

    @Serializable
    data object POSScreen: Route

    @Serializable
    data object TransactionHistoryScreen: Route

    @Serializable
    data object UserProfileRoute: Route

    @Serializable
    data object AdminMenuRoute: Route

    @Serializable
    data object AddItemToInventoryRoute: Route

    @Serializable
    data object BusinessListScreenRoute: Route

    @Serializable
    data object RoleManagementRoute: Route

    @Serializable
    data object CreateEditRoleRoute: Route


    @Serializable
    data object AddStaffRoute: Route

    @Serializable
    data object StaffListRoute: Route

    @Serializable
    data object PermissionManagementRoute: Route

    @Serializable
    data object BranchManagementRoute: Route

    @Serializable
    data object CreateEditDepartmentRoute: Route

    @Serializable
    data object DepartmentListRoute: Route

    @Serializable
    data object ProductListRoute: Route

    @Serializable
    data object CategoryListRoute: Route

    @Serializable
    data object AddCategory: Route



    @Serializable
    data class CategoryDetailRoute(val categoryId: String) : Route


    @Serializable
    data object LocationListRoute: Route

    @Serializable
    data object StockMovementListRoute: Route

    @Serializable
    data object AddProductRoute: Route

    @Serializable
    data class ProductDetailRoute(val productId:String): Route

    @Serializable
    data object StockTransferRoute: Route

    @Serializable
    data object AuditTrailRoute: Route

    @Serializable
    data class BookDetail(val id: String): Route

    @Serializable
    data class StaffDetailRoute(val staffId: String): Route

}