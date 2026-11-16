package com.sundroid.kobokobo.admin.data.domain.repository

import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Repository interface for managing roles and permissions
 */
interface RolePermissionRepository {
    
    // Role operations
    suspend fun createRole(role: Role): Result<Role, DataError>
    suspend fun updateRole(role: Role): Result<Role, DataError>
    suspend fun deleteRole(roleId: String, businessId: String): EmptyResult<DataError>
    suspend fun getRoleById(roleId: String, businessId: String): Result<Role?, DataError>
    suspend fun getAllRoles(businessId: String): Flow<List<Role>>
    suspend fun getRolesByUser(userId: String, businessId: String): Result<List<Role>, DataError>
    
    // Permission operations
    suspend fun createPermission(permission: Permission): Result<Permission, DataError>
    suspend fun updatePermission(permission: Permission): Result<Permission, DataError>
    suspend fun deletePermission(permissionId: String): EmptyResult<DataError>
    suspend fun getPermissionById(permissionId: String): Result<Permission?, DataError>
    suspend fun getAllPermissions(): Flow<List<Permission>>
    suspend fun getPermissionsByCategory(category: String): Flow<List<Permission>>
    
    // Role-Permission operations
    suspend fun assignPermissionsToRole(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError>
    suspend fun removePermissionsFromRole(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError>
    suspend fun getRolePermissions(roleId: String, businessId: String): Result<List<Permission>, DataError>
    
    // User-Role operations
    suspend fun assignRoleToUser(userId: String, businessId: String, roleId: String): EmptyResult<DataError>
    suspend fun removeRoleFromUser(userId: String, businessId: String, roleId: String): EmptyResult<DataError>
    suspend fun getUserRoles(userId: String, businessId: String): Result<List<Role>, DataError>
    suspend fun getUserPermissions(userId: String, businessId: String): Result<List<Permission>, DataError>
    
    // Business setup
    suspend fun initializeBusinessRoles(businessId: String, createdBy: String): EmptyResult<DataError>
    suspend fun initializeSystemPermissions(): EmptyResult<DataError>
    suspend fun getRolePermissionsList(roleId: String, businessId: String): Result<List<String>, DataError>

}

