package com.sundroid.kobokobo.admin.data.repository

import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.repository.FirebaseRolePermissionRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.FirebaseError
import com.sundroid.kobokobo.core.domain.toDataErrorResult
import com.sundroid.kobokobo.core.domain.toFirebaseErrorResult

/**
 * Example usage of RolePermissionRepository with different error types
 */
class RolePermissionUsageExample(
    private val repository: FirebaseRolePermissionRepository
) {
    
    /**
     * Example using DataError (recommended for most use cases)
     */
    suspend fun createRoleWithDataError(role: Role): String {
        return when (val result = repository.createRole(role)) {
            is com.sundroid.kobokobo.core.domain.Result.Success -> {
                "Role created successfully: ${result.data.name}"
            }
            is com.sundroid.kobokobo.core.domain.Result.Error -> {
                when (result.error) {
                    is DataError.Remote -> "Network error: ${result.error.name}"
                    is DataError.Local -> "Local error: ${result.error.name}"
                }
            }
        }
    }
    
    /**
     * Example using FirebaseError (for Firebase-specific error handling)
     */
    suspend fun createRoleWithFirebaseError(role: Role): String {
        return when (val result = repository.createRoleWithFirebaseError(role)) {
            is com.sundroid.kobokobo.core.domain.Result.Success -> {
                "Role created successfully: ${result.data.name}"
            }
            is com.sundroid.kobokobo.core.domain.Result.Error -> {
                when (result.error) {
                    is FirebaseError.Auth -> "Authentication error: ${result.error.name}"
                    is FirebaseError.Firestore -> "Database error: ${result.error.name}"
                    is FirebaseError.Storage -> "Storage error: ${result.error.name}"
                }
            }
        }
    }
    
    /**
     * Example of converting between error types
     */
    suspend fun createRoleWithConversion(role: Role): String {
        // Use Firebase-specific method
        val firebaseResult = repository.createRoleWithFirebaseError(role)
        
        // Convert to DataError for consistent handling
        val dataErrorResult = firebaseResult.toDataErrorResult()
        
        return when (dataErrorResult) {
            is com.sundroid.kobokobo.core.domain.Result.Success -> {
                "Role created successfully: ${dataErrorResult.data.name}"
            }
            is com.sundroid.kobokobo.core.domain.Result.Error -> {
                when (dataErrorResult.error) {
                    is DataError.Remote -> "Network error: ${dataErrorResult.error.name}"
                    is DataError.Local -> "Local error: ${dataErrorResult.error.name}"
                }
            }
        }
    }
    
    /**
     * Example of chaining operations with error handling
     */
    suspend fun createRoleAndAssignPermissions(role: Role, permissionIds: List<String>): String {
        return when (val createResult = repository.createRole(role)) {
            is com.sundroid.kobokobo.core.domain.Result.Success -> {
                val assignResult = repository.assignPermissionsToRole(
                    roleId = createResult.data.id,
                    businessId = createResult.data.businessId,
                    permissionIds = permissionIds
                )
                
                when (assignResult) {
                    is com.sundroid.kobokobo.core.domain.Result.Success -> {
                        "Role created and permissions assigned successfully"
                    }
                    is com.sundroid.kobokobo.core.domain.Result.Error -> {
                        "Role created but failed to assign permissions: ${assignResult.error}"
                    }
                }
            }
            is com.sundroid.kobokobo.core.domain.Result.Error -> {
                "Failed to create role: ${createResult.error}"
            }
        }
    }
}














