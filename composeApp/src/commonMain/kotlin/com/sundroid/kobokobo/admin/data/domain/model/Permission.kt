package com.sundroid.kobokobo.admin.data.domain.model

import com.google.firebase.firestore.DocumentId

/**
 * Represents a permission that can be assigned to roles
 */
data class Permission(
    @DocumentId val id: String = "", // Firestore will populate this from the document ID
    val name: String = "",
    val description: String = "",
    val category: PermissionCategory = PermissionCategory.STAFF,
    val isSystemPermission: Boolean = false
)

/**
 * Categories for organizing permissions
 */
enum class PermissionCategory(val displayName: String) {
    INVENTORY("Inventory Management"),
    SALES("Sales & Transactions"),
    STAFF("Staff Management"),
    BUSINESS("Business Settings"),
    REPORTS("Reports & Analytics"),
    CUSTOMER("Customer Management"),
    PRODUCT("Product Management"),
    EXPENSES("Expenses Management"),
    SYSTEM("System Administration"),
    HOTELS("Hotels And Lodgings"),
    DAMAGES("Damages"),
    LAUNDRY("Laundry Management"),
    SERVICES("Services Management")
}
enum class PermissionCategory1(val displayName: String) {
    INVENTORY("Inventory Management"),
    SALES("Sales & Transactions"),
    STAFF("Staff Management"),
    BUSINESS("Business Settings"),
    REPORTS("Reports & Analytics"),
    CUSTOMER("Customer Management"),
    PRODUCT("Product Management"),
    EXPENSES("Expenses Management"),
    SYSTEM("System Administration"),
    SERVICES("Services Management")
}

/**
 * Predefined system permissions
 */
object SystemPermissions {
    // Inventory Management
    val VIEW_INVENTORY = Permission(
        id = "view_inventory",
        name = "View Inventory",
        description = "View inventory items and stock levels",
        category = PermissionCategory.INVENTORY,
        isSystemPermission = true
    )
    
    val ADD_INVENTORY = Permission(
        id = "add_inventory",
        name = "Add Inventory Items",
        description = "Add new items to inventory",
        category = PermissionCategory.INVENTORY,
        isSystemPermission = true
    )
    
    val EDIT_INVENTORY = Permission(
        id = "edit_inventory",
        name = "Edit Inventory Items",
        description = "Modify existing inventory items",
        category = PermissionCategory.INVENTORY,
        isSystemPermission = true
    )
    
    val DELETE_INVENTORY = Permission(
        id = "delete_inventory",
        name = "Delete Inventory Items",
        description = "Remove items from inventory",
        category = PermissionCategory.INVENTORY,
        isSystemPermission = true
    )
    
    val MANAGE_STOCK = Permission(
        id = "manage_stock",
        name = "Manage Stock Levels",
        description = "Update stock quantities and reorder levels",
        category = PermissionCategory.INVENTORY,
        isSystemPermission = true
    )
    
    // Sales & Transactions
    val VIEW_SALES = Permission(
        id = "view_sales",
        name = "View Sales",
        description = "View sales transactions and history",
        category = PermissionCategory.SALES,
        isSystemPermission = true
    )
    
    val PROCESS_SALES = Permission(
        id = "process_sales",
        name = "Process Sales",
        description = "Create and process sales transactions",
        category = PermissionCategory.SALES,
        isSystemPermission = true
    )
    
    val REFUND_SALES = Permission(
        id = "refund_sales",
        name = "Process Refunds",
        description = "Process refunds and returns",
        category = PermissionCategory.SALES,
        isSystemPermission = true
    )
    
    val DELETE_SALES = Permission(
        id = "delete_sales",
        name = "Delete Sales",
        description = "Delete processed sales transactions",
        category = PermissionCategory.SALES,
        isSystemPermission = true
    )
    
    val VIEW_REPORTS = Permission(
        id = "view_reports",
        name = "View Reports",
        description = "Access sales and inventory reports",
        category = PermissionCategory.REPORTS,
        isSystemPermission = true
    )
    
    // Staff Management
    val VIEW_STAFF = Permission(
        id = "view_staff",
        name = "View Staff",
        description = "View staff members and their information",
        category = PermissionCategory.STAFF,
        isSystemPermission = true
    )
    
    val ADD_STAFF = Permission(
        id = "add_staff",
        name = "Add Staff",
        description = "Add new staff members to the business",
        category = PermissionCategory.STAFF,
        isSystemPermission = true
    )
    
    val EDIT_STAFF = Permission(
        id = "edit_staff",
        name = "Edit Staff",
        description = "Modify staff member information",
        category = PermissionCategory.STAFF,
        isSystemPermission = true
    )
    
    val REMOVE_STAFF = Permission(
        id = "remove_staff",
        name = "Remove Staff",
        description = "Remove staff members from the business",
        category = PermissionCategory.STAFF,
        isSystemPermission = true
    )
    
    val MANAGE_ROLES = Permission(
        id = "manage_roles",
        name = "Manage Roles & Permissions",
        description = "Create, edit, and manage roles and permissions",
        category = PermissionCategory.STAFF,
        isSystemPermission = true
    )
    
    // Business Settings
    val VIEW_BUSINESS_SETTINGS = Permission(
        id = "view_business_settings",
        name = "View Business Settings",
        description = "View business configuration and settings",
        category = PermissionCategory.BUSINESS,
        isSystemPermission = true
    )
    
    val EDIT_BUSINESS_SETTINGS = Permission(
        id = "edit_business_settings",
        name = "Edit Business Settings",
        description = "Modify business configuration and settings",
        category = PermissionCategory.BUSINESS,
        isSystemPermission = true
    )
    
    // Customer Management
    val VIEW_CUSTOMERS = Permission(
        id = "view_customers",
        name = "View Customers",
        description = "View customer information and history",
        category = PermissionCategory.CUSTOMER,
        isSystemPermission = true
    )
    
    val ADD_CUSTOMERS = Permission(
        id = "add_customers",
        name = "Add Customers",
        description = "Add new customers to the system",
        category = PermissionCategory.CUSTOMER,
        isSystemPermission = true
    )
    
    val EDIT_CUSTOMERS = Permission(
        id = "edit_customers",
        name = "Edit Customers",
        description = "Modify customer information",
        category = PermissionCategory.CUSTOMER,
        isSystemPermission = true
    )
    
    // Product Management
    val VIEW_PRODUCTS = Permission(
        id = "view_products",
        name = "View Products",
        description = "View product catalog and details",
        category = PermissionCategory.PRODUCT,
        isSystemPermission = true
    )
    
    val ADD_PRODUCTS = Permission(
        id = "add_products",
        name = "Add Products",
        description = "Add new products to the catalog",
        category = PermissionCategory.PRODUCT,
        isSystemPermission = true
    )
    
    val EDIT_PRODUCTS = Permission(
        id = "edit_products",
        name = "Edit Products",
        description = "Modify product information",
        category = PermissionCategory.PRODUCT,
        isSystemPermission = true
    )
    
    val DELETE_PRODUCTS = Permission(
        id = "delete_products",
        name = "Delete Products",
        description = "Remove products from the catalog",
        category = PermissionCategory.PRODUCT,
        isSystemPermission = true
    )
    // Expenses Management
    val VIEW_EXPENSES = Permission(
        id = "view_expenses",
        name = "View Expenses",
        description = "View expense catalog and details",
        category = PermissionCategory.EXPENSES,
        isSystemPermission = true
    )

    val ADD_EXPENSES = Permission(
        id = "add_expenses",
        name = "Add Expenses",
        description = "Add new expenses to the catalog",
        category = PermissionCategory.EXPENSES,
        isSystemPermission = true
    )

    val EDIT_EXPENSES = Permission(
        id = "edit_expenses",
        name = "Edit Expenses",
        description = "Modify expenses information",
        category = PermissionCategory.EXPENSES,
        isSystemPermission = true
    )

    val DELETE_EXPENSES = Permission(
        id = "delete_expenses",
        name = "Delete Expenses",
        description = "Remove expenses from the catalog",
        category = PermissionCategory.EXPENSES,
        isSystemPermission = true
    )
    val VIEW_HOTEL_BOOKINGS = Permission(
        id = "view_hotel_bookings",
        name = "View Hotel Bookings",
        description = "View hotel bookings",
        category = PermissionCategory.HOTELS,
        isSystemPermission = true
    )

    val ADD_HOTEL_BOOKINGS = Permission(
        id = "add_hotel_bookings",
        name = "Add Hotel Bookings",
        description = "Add Hotel Bookings",
        category = PermissionCategory.HOTELS,
        isSystemPermission = true
    )

    val EDIT_HOTEL_BOOKINGS = Permission(
        id = "edit_hotel_bookings",
        name = "Edit Hotel Bookings",
        description = "Modify Hotels Information",
        category = PermissionCategory.HOTELS,
        isSystemPermission = true
    )

    val DELETE_BOOKINGS = Permission(
        id = "delete_hotel_bookings",
        name = "Delete Hotel Bookings",
        description = "Remove hotel bookings  from the catalog",
        category = PermissionCategory.HOTELS,
        isSystemPermission = true
    )
    val MANAGE_ROOMS = Permission(
        id = "manage_hotel_rooms",
        name = "Manage Hotel Rooms",
        description = "Manage hotel rooms",
        category = PermissionCategory.HOTELS,
        isSystemPermission = true
    )
    val VIEW_DAMAGES = Permission(
        id = "view_damages",
        name = "View Damages",
        description = "View damages",
        category = PermissionCategory.DAMAGES,
        isSystemPermission = true
    )

    val ADD_DAMAGES = Permission(
        id = "add_damages",
        name = "Add Damages",
        description = "Add Damages",
        category = PermissionCategory.DAMAGES,
        isSystemPermission = true
    )

    val EDIT_DAMAGES = Permission(
        id = "edit_damages",
        name = "Edit Damages",
        description = "Modify Damages",
        category = PermissionCategory.DAMAGES,
        isSystemPermission = true
    )

    val DELETE_DAMAGES = Permission(
        id = "delete_damages",
        name = "Delete Damages",
        description = "Remove Damages",
        category = PermissionCategory.DAMAGES,
        isSystemPermission = true
    )
    
    // Laundry Management
    val VIEW_LAUNDRY = Permission(
        id = "view_laundry",
        name = "View Laundry",
        description = "View laundry services and materials",
        category = PermissionCategory.LAUNDRY,
        isSystemPermission = true
    )

    val MANAGE_LAUNDRY_MATERIALS = Permission(
        id = "manage_laundry_materials",
        name = "Manage Laundry Materials",
        description = "Add, edit, or delete laundry materials and prices",
        category = PermissionCategory.LAUNDRY,
        isSystemPermission = true
    )

    val PROCESS_LAUNDRY_SALES = Permission(
        id = "process_laundry_sales",
        name = "Process Laundry Sales",
        description = "Create and process laundry sales transactions",
        category = PermissionCategory.LAUNDRY,
        isSystemPermission = true
    )

    // services Management
    val CREATE_SERVICES_ITEMS = Permission(
        id = "create_services",
        name = "Create Services",
        description = "Create Services Items",
        category = PermissionCategory.SERVICES,
        isSystemPermission = true
    )

    val BOOK_APPOINTMENT = Permission(
        id = "book_appointment",
        name = "Book Appointment",
        description = "Book appointment",
        category = PermissionCategory.SERVICES,
        isSystemPermission = true
    )
    val COMPLETE_APPOINTMENT = Permission(
        id = "complete_appointment",
        name = "Complete Appointment",
        description = "Complete appointment",
        category = PermissionCategory.SERVICES,
        isSystemPermission = true
    )

    val CAN_VIEW_OTHER_STAFF_APPOINTMENT = Permission(
        id = "can_view_other_staff_appointment",
        name = "Can View Other Staff Appointment",
        description = "Can View Other Stag appointment",
        category = PermissionCategory.SERVICES,
        isSystemPermission = true
    )

    val PROCESS_SERVICES_SALES = Permission(
        id = "process_services_sales",
        name = "Process services Sales",
        description = "Create and process services sales transactions",
        category = PermissionCategory.SERVICES,
        isSystemPermission = true
    )

    // System Administration
    val SYSTEM_ADMIN = Permission(
        id = "system_admin",
        name = "System Administrator",
        description = "Full system access and administration",
        category = PermissionCategory.SYSTEM,
        isSystemPermission = true
    )
    
    /**
     * Get all system permissions
     */
    fun getAllPermissions(): List<Permission> {
        return listOf(
           // VIEW_INVENTORY, ADD_INVENTORY, EDIT_INVENTORY, DELETE_INVENTORY, MANAGE_STOCK,
            VIEW_SALES, PROCESS_SALES, REFUND_SALES, DELETE_SALES, VIEW_REPORTS,
          //  VIEW_STAFF, ADD_STAFF, EDIT_STAFF, REMOVE_STAFF, MANAGE_ROLES,
          //  VIEW_BUSINESS_SETTINGS, EDIT_BUSINESS_SETTINGS,
         //   VIEW_CUSTOMERS, ADD_CUSTOMERS, EDIT_CUSTOMERS,
         //   VIEW_PRODUCTS, ADD_PRODUCTS, EDIT_PRODUCTS, DELETE_PRODUCTS,
            VIEW_EXPENSES, ADD_EXPENSES, EDIT_EXPENSES, DELETE_EXPENSES,
            VIEW_DAMAGES, ADD_DAMAGES, EDIT_DAMAGES, DELETE_DAMAGES,
            MANAGE_ROOMS,
            VIEW_HOTEL_BOOKINGS, ADD_HOTEL_BOOKINGS, EDIT_HOTEL_BOOKINGS, DELETE_BOOKINGS,
            VIEW_LAUNDRY, MANAGE_LAUNDRY_MATERIALS, PROCESS_LAUNDRY_SALES,
            CREATE_SERVICES_ITEMS, BOOK_APPOINTMENT, CAN_VIEW_OTHER_STAFF_APPOINTMENT,  PROCESS_SERVICES_SALES, COMPLETE_APPOINTMENT,
            SYSTEM_ADMIN
        )
    }
    
    /**
     * Get permissions by category
     */
    fun getPermissionsByCategory(category: PermissionCategory): List<Permission> {
        return getAllPermissions().filter { it.category == category }
    }
}

