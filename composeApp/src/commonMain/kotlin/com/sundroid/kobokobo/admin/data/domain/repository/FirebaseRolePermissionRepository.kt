package com.sundroid.kobokobo.admin.data.domain.repository

import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.FirebaseError
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Firebase-specific repository interface for managing roles and permissions
 * Extends the base repository with Firebase-specific error handling
 */
interface FirebaseRolePermissionRepository : RolePermissionRepository {
    
    // Firebase-specific role operations with Firebase error types
    suspend fun createRoleWithFirebaseError(role: Role): Result<Role, FirebaseError>
    suspend fun updateRoleWithFirebaseError(role: Role): Result<Role, FirebaseError>
    suspend fun deleteRoleWithFirebaseError(roleId: String, businessId: String): EmptyResult<FirebaseError>
    suspend fun getRoleByIdWithFirebaseError(roleId: String, businessId: String): Result<Role?, FirebaseError>
    suspend fun getRolesByUserWithFirebaseError(userId: String, businessId: String): Result<List<Role>, FirebaseError>
    
    // Firebase-specific permission operations
    suspend fun createPermissionWithFirebaseError(permission: Permission): Result<Permission, FirebaseError>
    suspend fun updatePermissionWithFirebaseError(permission: Permission): Result<Permission, FirebaseError>
    suspend fun deletePermissionWithFirebaseError(permissionId: String): EmptyResult<FirebaseError>
    suspend fun getPermissionByIdWithFirebaseError(permissionId: String): Result<Permission?, FirebaseError>
    
    // Firebase-specific role-permission operations
    suspend fun assignPermissionsToRoleWithFirebaseError(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<FirebaseError>
    suspend fun removePermissionsFromRoleWithFirebaseError(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<FirebaseError>
    suspend fun getRolePermissionsWithFirebaseError(roleId: String, businessId: String): Result<List<Permission>, FirebaseError>
    
    // Firebase-specific user-role operations
    suspend fun assignRoleToUserWithFirebaseError(userId: String, businessId: String, roleId: String): EmptyResult<FirebaseError>
    suspend fun removeRoleFromUserWithFirebaseError(userId: String, businessId: String, roleId: String): EmptyResult<FirebaseError>
    suspend fun getUserRolesWithFirebaseError(userId: String, businessId: String): Result<List<Role>, FirebaseError>
    suspend fun getUserPermissionsWithFirebaseError(userId: String, businessId: String): Result<List<Permission>, FirebaseError>
    
    // Firebase-specific business setup
    suspend fun initializeBusinessRolesWithFirebaseError(businessId: String, createdBy: String): EmptyResult<FirebaseError>
    suspend fun initializeSystemPermissionsWithFirebaseError(): EmptyResult<FirebaseError>
}

