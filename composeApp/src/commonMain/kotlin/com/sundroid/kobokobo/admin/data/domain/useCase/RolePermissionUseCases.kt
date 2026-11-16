package com.sundroid.kobokobo.admin.data.domain.useCase

import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.repository.RolePermissionRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

/**
 * Use case for creating a new role
 */
class CreateRoleUseCase(
    private val repository: RolePermissionRepository
)
{
    suspend operator fun invoke(role: Role): Result<Role, DataError> {
        return repository.createRole(role)
    }
}

/**
 * Use case for updating an existing role
 */
class UpdateRoleUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(role: Role): Result<Role, DataError> {
        return repository.updateRole(role)
    }
}

/**
 * Use case for deleting a role
 */
class DeleteRoleUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String): EmptyResult<DataError> {
        return repository.deleteRole(roleId, businessId)
    }
}

/**
 * Use case for getting all roles for a business
 */
class GetAllRolesUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(businessId: String): Flow<List<Role>> {
        return repository.getAllRoles(businessId)
    }
}

/**
 * Use case for getting a specific role
 */
class GetRoleByIdUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String): Result<Role?, DataError> {
        return repository.getRoleById(roleId, businessId)
    }
}

/**
 * Use case for getting all permissions
 */
class GetAllPermissionsUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(): Flow<List<Permission>> {
        return repository.getAllPermissions()
    }
}

/**
 * Use case for getting permissions by category
 */
class GetPermissionsByCategoryUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(category: String): Flow<List<Permission>> {
        return repository.getPermissionsByCategory(category)
    }
}

/**
 * Use case for assigning permissions to a role
 */
class AssignPermissionsToRoleUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError> {
        return repository.assignPermissionsToRole(roleId, businessId, permissionIds)
    }
}

/**
 * Use case for removing permissions from a role
 */
class RemovePermissionsFromRoleUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError> {
        return repository.removePermissionsFromRole(roleId, businessId, permissionIds)
    }
}

/**
 * Use case for getting permissions for a specific role
 */
class GetRolePermissionsUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String): Result<List<Permission>, DataError> {
        return repository.getRolePermissions(roleId, businessId)
    }
}
class GetRolePermissionsList(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(roleId: String, businessId: String): Result<List<String>, DataError> {
        return repository.getRolePermissionsList(roleId,businessId)
    }
}

/**
 * Use case for assigning a role to a user
 */
class AssignRoleToUserUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(userId: String, businessId: String, roleId: String): EmptyResult<DataError> {
        return repository.assignRoleToUser(userId, businessId, roleId)
    }
}

/**
 * Use case for removing a role from a user
 */
class RemoveRoleFromUserUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(userId: String, businessId: String, roleId: String): EmptyResult<DataError> {
        return repository.removeRoleFromUser(userId, businessId, roleId)
    }
}

/**
 * Use case for getting roles for a specific user
 */
class GetUserRolesUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(userId: String, businessId: String): Result<List<Role>, DataError> {
        return repository.getUserRoles(userId, businessId)
    }
}

/**
 * Use case for getting permissions for a specific user
 */
class GetUserPermissionsUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(userId: String, businessId: String): Result<List<Permission>, DataError> {
        return repository.getUserPermissions(userId, businessId)
    }
}

/**
 * Use case for initializing business roles
 */
class InitializeBusinessRolesUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(businessId: String, createdBy: String): EmptyResult<DataError> {
        return repository.initializeBusinessRoles(businessId, createdBy)
    }
}

/**
 * Use case for initializing system permissions
 */
class InitializeSystemPermissionsUseCase(
    private val repository: RolePermissionRepository
) {
    suspend operator fun invoke(): EmptyResult<DataError> {
        return repository.initializeSystemPermissions()
    }
}

