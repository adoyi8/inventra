package com.sundroid.kobokobo.admin.data.domain.model

import java.util.UUID

/**
 * Represents a role that can be assigned to users
 */
data class Role(
    val id: String = UUID.randomUUID().toString(),
    val name: String = "",
    val description: String = "",
    val businessId: String = "",
    val permissions: List<String> = emptyList(), // List of permission IDs
    val isSystemRole: Boolean = false, // System roles cannot be deleted
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val createdBy: String? = null,
    val updatedBy: String? = null
)

/**
 * Predefined system roles
 */
object SystemRoles {
    val MANAGER = Role(
        id = "manager",
        name = "Manager",
        description = "Full access to all business operations",
        businessId = "", // Will be set when business is created
        permissions = SystemPermissions.getAllPermissions().map { it.id },
        isSystemRole = true
    )
    
    val EMPLOYEE = Role(
        id = "employee",
        name = "Employee",
        description = "Basic access for daily operations",
        businessId = "", // Will be set when business is created
        permissions = listOf(
            SystemPermissions.VIEW_INVENTORY.id,
            SystemPermissions.VIEW_SALES.id,
            SystemPermissions.PROCESS_SALES.id,
            SystemPermissions.VIEW_CUSTOMERS.id,
            SystemPermissions.ADD_CUSTOMERS.id,
            SystemPermissions.VIEW_PRODUCTS.id
        ),
        isSystemRole = true
    )
    
    val CASHIER = Role(
        id = "cashier",
        name = "Cashier",
        description = "Point of sale operations",
        businessId = "", // Will be set when business is created
        permissions = listOf(
            SystemPermissions.VIEW_INVENTORY.id,
            SystemPermissions.VIEW_SALES.id,
            SystemPermissions.PROCESS_SALES.id,
            SystemPermissions.REFUND_SALES.id,
            SystemPermissions.VIEW_CUSTOMERS.id,
            SystemPermissions.ADD_CUSTOMERS.id,
            SystemPermissions.VIEW_PRODUCTS.id
        ),
        isSystemRole = true
    )
    
    val INVENTORY_CLERK = Role(
        id = "inventory_clerk",
        name = "Inventory Clerk",
        description = "Inventory management operations",
        businessId = "", // Will be set when business is created
        permissions = listOf(
            SystemPermissions.VIEW_INVENTORY.id,
            SystemPermissions.ADD_INVENTORY.id,
            SystemPermissions.EDIT_INVENTORY.id,
            SystemPermissions.MANAGE_STOCK.id,
            SystemPermissions.VIEW_PRODUCTS.id,
            SystemPermissions.ADD_PRODUCTS.id,
            SystemPermissions.EDIT_PRODUCTS.id
        ),
        isSystemRole = true
    )

    val RECEPTIONIST = Role(
        id = "receptionist",
        name = "Receptionist",
        description = "Hotel front desk operations",
        businessId = "",
        permissions = listOf(
            // Define specific permissions for Receptionist if SystemPermissions allows, 
            // otherwise reuse relevant or leave empty if handled by UI logic for now.
            // Assuming we might need to add HOTEL_MANAGEMENT permissions later.
            // For now, giving basic read access and specific hotel UI access will be handled in UI.
             SystemPermissions.VIEW_HOTEL_BOOKINGS.id,
             SystemPermissions.VIEW_HOTEL_BOOKINGS.id,
             SystemPermissions.ADD_HOTEL_BOOKINGS.id,
            SystemPermissions.DELETE_BOOKINGS.id

        ),
        isSystemRole = true
    )
    
    /**
     * Get all system roles
     */
    fun getAllSystemRoles(): List<Role> {
        return listOf(MANAGER, EMPLOYEE, CASHIER, INVENTORY_CLERK, RECEPTIONIST)
    }
    
    /**
     * Create system roles for a specific business
     */
    fun createSystemRolesForBusiness(businessId: String, createdBy: String): List<Role> {
        return getAllSystemRoles().map { role ->
            role.copy(
                businessId = businessId,
                createdBy = createdBy,
                updatedBy = createdBy
            )
        }
    }
}

