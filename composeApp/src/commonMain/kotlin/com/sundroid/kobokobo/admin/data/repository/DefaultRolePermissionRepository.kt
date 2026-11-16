package com.sundroid.kobokobo.admin.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.admin.data.domain.model.Permission
import com.sundroid.kobokobo.admin.data.domain.model.Role
import com.sundroid.kobokobo.admin.data.domain.model.SystemPermissions
import com.sundroid.kobokobo.admin.data.domain.model.SystemRoles
import com.sundroid.kobokobo.admin.data.domain.repository.RolePermissionRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.tasks.await

class DefaultRolePermissionRepository(
    private val firestore: FirebaseFirestore
) : RolePermissionRepository {

    companion object {
        private const val ROLES_COLLECTION = "roles"
        private const val PERMISSIONS_COLLECTION = "permissions"
        private const val USER_ROLES_COLLECTION = "user_roles"
    }

    // Role operations
    override suspend fun createRole(role: Role): Result<Role, DataError> = runCatching {
        val roleRef = firestore.collection(BUSINESS_COLLECTION).document(role.businessId).collection(ROLES_COLLECTION).document(role.id)
        roleRef.set(role).await()
        role
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun updateRole(role: Role): Result<Role, DataError> = runCatching {
        val roleRef = firestore.collection(BUSINESS_COLLECTION).document(role.businessId).collection(ROLES_COLLECTION).document(role.id)
        val updatedRole = role.copy(updatedAt = System.currentTimeMillis())
        roleRef.set(updatedRole).await()
        updatedRole
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun deleteRole(roleId: String, businessId: String): EmptyResult<DataError> = runCatching {
        val roleRef = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION).document(roleId)
        val roleDoc = roleRef.get().await()
        
        if (!roleDoc.exists()) {
            throw Exception("Role not found")
        }
        
        val role = roleDoc.toObject(Role::class.java)
        if (role?.isSystemRole == true) {
            throw Exception("Cannot delete system roles")
        }
        
        if (role?.businessId != businessId) {
            throw Exception("Role does not belong to this business")
        }
        
        roleRef.delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun getRoleById(roleId: String, businessId: String): Result<Role?, DataError> = runCatching {
        val roleDoc = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION)
            .document(roleId)
            .get()
            .await()
        
        if (!roleDoc.exists()) {
            null
        } else {
            val role = roleDoc.toObject(Role::class.java)
            if (role?.businessId == businessId) role else null
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}

    )

    override suspend fun getAllRoles(businessId: String): Flow<List<Role>> = flow {
        // Run both Firestore queries in parallel for efficiency
        val (businessRolesSnapshot, systemRolesSnapshot) = coroutineScope {
            val businessRolesDeferred = async {
                firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION)
                    .whereEqualTo("businessId", businessId)
                    .get()
                    .await()
            }

            val systemRolesDeferred = async {
                firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION)
                    .whereEqualTo("isSystemRole", true)
                    .get()
                    .await()
            }

            // Await the results of both queries
            Pair(businessRolesDeferred.await(), systemRolesDeferred.await())
        }

        // Use a map to combine results and automatically handle duplicates by document ID
        val rolesMap = mutableMapOf<String, Role>()

        // Process roles specific to the business
        businessRolesSnapshot.documents.mapNotNull { doc ->
            doc.toObject(Role::class.java)?.let { rolesMap[doc.id] = it }
        }

        // Process system roles, overwriting any duplicates (e.g., a system role that also has a businessId)
        systemRolesSnapshot.documents.mapNotNull { doc ->
            doc.toObject(Role::class.java)?.let { rolesMap[doc.id] = it }
        }

        // Since Firestore can't sort the combined result, sort it on the client-side.
        val finalRoles = rolesMap.values.sortedBy { it.createdAt }

        emit(finalRoles)
    }

    override suspend fun getRolesByUser(userId: String, businessId: String): Result<List<Role>, DataError> = runCatching {
        val userRolesSnapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(USER_ROLES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("businessId", businessId)
            .get()
            .await()
        
        val roleIds = userRolesSnapshot.documents.mapNotNull { doc ->
            doc.getString("roleId")
        }
        
        if (roleIds.isEmpty()) {
            emptyList()
        } else {
            val rolesSnapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION)
                .whereIn("id", roleIds)
                .whereEqualTo("businessId", businessId)
                .get()
                .await()
            
            rolesSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Role::class.java)
            }
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}

    )

    // Permission operations
    override suspend fun createPermission(permission: Permission): Result<Permission, DataError> = runCatching {
        val permissionRef = firestore.collection(PERMISSIONS_COLLECTION).document(permission.id)
        permissionRef.set(permission).await()
        permission
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun updatePermission(permission: Permission): Result<Permission, DataError> = runCatching {
        val permissionRef = firestore.collection(PERMISSIONS_COLLECTION).document(permission.id)
        permissionRef.set(permission).await()
        permission
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun deletePermission(permissionId: String): EmptyResult<DataError> = runCatching {
        val permissionRef = firestore.collection(PERMISSIONS_COLLECTION).document(permissionId)
        val permissionDoc = permissionRef.get().await()
        
        if (!permissionDoc.exists()) {
            throw Exception("Permission not found")
        }
        
        val permission = permissionDoc.toObject(Permission::class.java)
        if (permission?.isSystemPermission == true) {
            throw Exception("Cannot delete system permissions")
        }
        
        permissionRef.delete().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun getPermissionById(permissionId: String): Result<Permission?, DataError> = runCatching {
        val permissionDoc = firestore.collection(PERMISSIONS_COLLECTION)
            .document(permissionId)
            .get()
            .await()
        
        if (permissionDoc.exists()) {
            permissionDoc.toObject(Permission::class.java)
        } else {
            null
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun getAllPermissions(): Flow<List<Permission>> = flow {
        val permissionsSnapshot = firestore.collection(PERMISSIONS_COLLECTION)
            .orderBy("category", Query.Direction.ASCENDING)
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()
        
        val permissions = permissionsSnapshot.documents.mapNotNull { doc ->
            doc.toObject(Permission::class.java)
        }
        emit(permissions)
    }

    override suspend fun getPermissionsByCategory(category: String): Flow<List<Permission>> = flow {
        val permissionsSnapshot = firestore.collection(PERMISSIONS_COLLECTION)
            .whereEqualTo("category", category)
            .orderBy("name", Query.Direction.ASCENDING)
            .get()
            .await()
        
        val permissions = permissionsSnapshot.documents.mapNotNull { doc ->
            doc.toObject(Permission::class.java)
        }
        emit(permissions)
    }

    // Role-Permission operations
    override suspend fun assignPermissionsToRole(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError> = runCatching {
        val roleRef = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION).document(roleId)
        val roleDoc = roleRef.get().await()
        
        if (!roleDoc.exists()) {
            throw Exception("Role not found")
        }
        
        val role = roleDoc.toObject(Role::class.java)
        if (role?.businessId != businessId) {
            throw Exception("Role does not belong to this business")
        }
        
        val updatedPermissions = (role.permissions + permissionIds).distinct()
        val updatedRole = role.copy(permissions = updatedPermissions, updatedAt = System.currentTimeMillis())
        
        roleRef.set(updatedRole).await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun removePermissionsFromRole(roleId: String, businessId: String, permissionIds: List<String>): EmptyResult<DataError> = runCatching {
        val roleRef = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION).document(roleId)
        val roleDoc = roleRef.get().await()
        
        if (!roleDoc.exists()) {
            throw Exception("Role not found")
        }
        
        val role = roleDoc.toObject(Role::class.java)
        if (role?.businessId != businessId) {
            throw Exception("Role does not belong to this business")
        }
        
        val updatedPermissions = role.permissions.filter { it !in permissionIds }
        val updatedRole = role.copy(permissions = updatedPermissions, updatedAt = System.currentTimeMillis())
        
        roleRef.set(updatedRole).await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun getRolePermissions(roleId: String, businessId: String): Result<List<Permission>, DataError> = runCatching {
        val roleDoc = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION)
            .document(roleId)
            .get()
            .await()
        
        if (!roleDoc.exists()) {
            throw Exception("Role not found")
        }
        
        val role = roleDoc.toObject(Role::class.java)
        if (role?.businessId != businessId) {
            throw Exception("Role does not belong to this business")
        }
        
        if (role.permissions.isEmpty()) {
            emptyList()
        } else {
            val permissionsSnapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(PERMISSIONS_COLLECTION)
                .whereIn("id", role.permissions)
                .get()
                .await()
            
            permissionsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Permission::class.java)
            }
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )
   override suspend fun getRolePermissionsList(roleId: String, businessId: String): Result<List<String>, DataError> = runCatching {
       val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
       val roleDoc = busRef.collection(ROLES_COLLECTION)
            .document(roleId)
            .get()
            .await()
        roleDoc.toObject(Role::class.java)
    }.fold(
        onSuccess = { Result.Success(it?.permissions?: emptyList()) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    // User-Role operations
    override suspend fun assignRoleToUser(userId: String, businessId: String, roleId: String): EmptyResult<DataError> = runCatching {
        val userRoleRef = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(USER_ROLES_COLLECTION).document()
        val userRole = mapOf(
            "userId" to userId,
            "businessId" to businessId,
            "roleId" to roleId,
            "assignedAt" to System.currentTimeMillis()
        )
        
        userRoleRef.set(userRole).await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun removeRoleFromUser(userId: String, businessId: String, roleId: String): EmptyResult<DataError> = runCatching {
        val userRolesSnapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(USER_ROLES_COLLECTION)
            .whereEqualTo("userId", userId)
            .whereEqualTo("businessId", businessId)
            .whereEqualTo("roleId", roleId)
            .get()
            .await()
        
        val batch = firestore.batch()
        userRolesSnapshot.documents.forEach { doc ->
            batch.delete(doc.reference)
        }
        batch.commit().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun getUserRoles(userId: String, businessId: String): Result<List<Role>, DataError> {
        return getRolesByUser(userId, businessId)
    }

    override suspend fun getUserPermissions(userId: String, businessId: String): Result<List<Permission>, DataError> = runCatching {
        val userRolesResult = getRolesByUser(userId, businessId)
        if (userRolesResult is Result.Error) {
            throw Exception(userRolesResult.error.toString())
        }
        
        val roles = (userRolesResult as Result.Success).data
        val allPermissionIds = roles.flatMap { it.permissions }.distinct()
        
        if (allPermissionIds.isEmpty()) {
            emptyList()
        } else {
            val permissionsSnapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(PERMISSIONS_COLLECTION)
                .whereIn("id", allPermissionIds)
                .get()
                .await()
            
            permissionsSnapshot.documents.mapNotNull { doc ->
                doc.toObject(Permission::class.java)
            }
        }
    }.fold(
        onSuccess = { Result.Success(it) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    // Business setup
    override suspend fun initializeBusinessRoles(businessId: String, createdBy: String): EmptyResult<DataError> = runCatching {
        val systemRoles = SystemRoles.createSystemRolesForBusiness(businessId, createdBy)
        
        val batch = firestore.batch()
        systemRoles.forEach { role ->
            val roleRef = firestore.collection(BUSINESS_COLLECTION).document(businessId).collection(ROLES_COLLECTION).document(role.id)
            batch.set(roleRef, role)
        }
        batch.commit().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )

    override suspend fun initializeSystemPermissions(): EmptyResult<DataError> = runCatching {
        val systemPermissions = SystemPermissions.getAllPermissions()
        
        val batch = firestore.batch()
        systemPermissions.forEach { permission ->
            val permissionRef = firestore.collection(PERMISSIONS_COLLECTION).document(permission.id)
            batch.set(permissionRef, permission)
        }
        batch.commit().await()
    }.fold(
        onSuccess = { Result.Success(Unit) },
        onFailure = { Result.Error(DataError.Remote.REQUEST_TIMEOUT)}
    )
}

