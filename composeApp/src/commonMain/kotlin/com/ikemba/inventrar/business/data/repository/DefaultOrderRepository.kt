package com.ikemba.inventrar.business.data.repository

import androidx.sqlite.SQLiteException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.business.data.database.BusinessDao
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.domain.model.AppUser
import com.ikemba.inventrar.business.data.domain.model.Staff
import com.ikemba.inventrar.business.data.domain.model.StaffRole
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.business.data.dto.SearchOrganizationResult
import com.ikemba.inventrar.business.network.RemoteBusinessDataSource
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.admin.data.domain.model.Department
import com.ikemba.inventrar.admin.data.domain.model.Branch
import com.ikemba.inventrar.admin.data.domain.model.SystemRoles
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.user.data.database.UserDao
import com.ikemba.inventrar.user.data.mappers.toUserEntity
import com.ikemba.inventrar.user.domain.User
import kotlinx.coroutines.tasks.await

class DefaultBusinessRepository(
    private val remoteBusinessDataSource: RemoteBusinessDataSource,
    private val firestore: FirebaseFirestore,
    private val auth: FirebaseAuth,
    private val businessDao: BusinessDao,
): BusinessRepository {
    override suspend fun getSingleHeldOrder(
        accessToken: String,
        request: VoidOrderRequest
    ): Result<SingleHeldOrderDto, DataError.Remote> {
        TODO("Not yet implemented")
    }


    override suspend fun getBusinesses(accessToken: String, paginationRequest: SearchOrganizationRequest): Result<OrganizationResponse, DataError.Remote> {
        return remoteBusinessDataSource.getBusiness(accessToken, paginationRequest)
    }

    override suspend fun createBusiness(accessToken: String, createBusinessRequest: CreateBusinessRequest): Result<ResponseDto, DataError.Remote> {
       return remoteBusinessDataSource.createBusiness(accessToken, createBusinessRequest)
    }

    override suspend fun voidOrder(
        accessToken: String,
        voidOrderRequest: VoidOrderRequest
    ): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }
    override suspend fun getBusinessesForUser(userId: String): kotlin.Result<List<Business>> = runCatching {
        // 1. Find all staff entries for the user
        val staffEntries = firestore.collection("staff")
            .whereEqualTo("userId", userId)
            .get().await().toObjects(Staff::class.java)
        if (staffEntries.isEmpty()) return kotlin.Result.success(emptyList<Business>())
        // 2. Get the business IDs from the staff entries
        val businessIds = staffEntries.map { it.businessId }
        // 3. Fetch all businesses corresponding to those IDs
        val businesses = firestore.collection("businesses")
            .whereIn("businessId", businessIds)
            .get().await().toObjects(Business::class.java)
        businesses
    }

    // Import SystemRoles if needed
// import com.example.yourproject.data.SystemRoles

    override suspend fun createBusiness(businessName: String, owner: FirebaseUser): kotlin.Result<Unit> = runCatching {
        val businessRef = firestore.collection("businesses").document()
        val businessId = businessRef.id

        val newBusiness = Business(
            businessId = businessId,
            name = businessName,
            ownerId = owner.uid
        )

        // Create default Admin department
        val departmentRef = firestore.collection("departments").document()
        val adminDepartment = Department(
            id = departmentRef.id,
            name = "Admin",
            description = "Default admin department",
            businessId = businessId,
            createdBy = owner.uid,
            updatedBy = owner.uid
        )

        // Create default Headquarters branch
        val branchRef = firestore.collection("branches").document()
        val hqBranch = Branch(
            id = branchRef.id,
            name = "Headquarters",
            description = "Default branch",
            businessId = businessId,
            createdBy = owner.uid,
            updatedBy = owner.uid
        )

        // --- MODIFICATIONS START ---

        // 1. Generate the default system roles for this business
        // (This assumes SystemRoles.createSystemRolesForBusiness returns the list of roles)
        val systemRoles = SystemRoles.createSystemRolesForBusiness(businessId, owner.uid)

        // 2. Find the "Manager" role from the list.
        // This search handles case-insensitivity and checks that it's a system role.
        val managerRole = systemRoles.firstOrNull {
            it.name.equals("Manager", ignoreCase = true) && it.isSystemRole
        } ?: throw IllegalStateException("System 'Manager' role could not be found or created.")

        // 3. Add the creator as a staff member using the found Manager Role ID
        val staffRef = firestore.collection("staff").document()
        val newStaffEntry = Staff(
            staffId = staffRef.id,
            businessId = businessId,
            userId = owner.uid,
            departmentId = adminDepartment.id,
            branchId = hqBranch.id,
            role = managerRole.id // <-- Assign the ID, not the string "manager"
        )

        // 4. Use a batch write for atomicity
        firestore.batch().apply {
            set(businessRef, newBusiness)
            set(departmentRef, adminDepartment)
            set(branchRef, hqBranch)
            set(staffRef, newStaffEntry)

            // 5. Add all the newly created system roles to the same batch
            systemRoles.forEach { role ->
                val roleRef = firestore.collection("roles").document(role.id)
                set(roleRef, role)
            }

        }.commit().await()
        // --- MODIFICATIONS END ---
    }
    override suspend fun findUserByEmail(email: String): kotlin.Result<AppUser?> = runCatching {
        val userDocument = firestore.collection("users")
            .whereEqualTo("email", email)
            .limit(1)
            .get().await()

        userDocument.documents.firstOrNull()?.toObject(AppUser::class.java)
    }

    override suspend fun addStaffToBusiness(user: AppUser, businessId: String, role: String, departmentId: String, branchId: String): kotlin.Result<Unit> = runCatching {
        val staffRef = firestore.collection("staff").document()
        val newStaffEntry = Staff(
            staffId = staffRef.id,
            businessId = businessId,
            userId = user.userId,
            departmentId = departmentId,
            branchId = branchId,
            role = role,
            active = true
        )
        staffRef.set(newStaffEntry).await()
    }
    override suspend fun saveBusinessInLocalDb(business: Business): EmptyResult<DataError.Local> {
        return try {
            businessDao.upsert(business)
            Result.Success(Unit)
        } catch(e: SQLiteException) {
            Result.Error(DataError.Local.DISK_FULL)
        }
    }
    override suspend fun getStaffRole(userId: String, businessId: String): kotlin.Result<Staff?> = runCatching {
        val staffQuery = firestore.collection("staff")
            .whereEqualTo("userId", userId)
            .whereEqualTo("businessId", businessId)
            .limit(1)
            .get()
            .await()

        if (staffQuery.isEmpty) {
            // This case should ideally not happen if the user can select the business,
            // but it's safe to handle. It means they aren't a staff member.
            null
        } else {
            val staffDocument = staffQuery.documents.first()
            staffDocument.toObject(Staff::class.java)
        }
    }

    override suspend fun getStaffForBusiness(businessId: String): kotlin.Result<List<Staff>> = runCatching {
        firestore.collection("staff")
            .whereEqualTo("businessId", businessId)
            .get()
            .await()
            .toObjects(Staff::class.java)
    }

    override suspend fun updateStaffRole(staffId: String, newRole: String): kotlin.Result<Unit> = runCatching {
        firestore.collection("staff")
            .document(staffId)
            .update(mapOf("role" to newRole))
            .await()
    }

    override suspend fun setStaffActive(staffId: String, active: Boolean): kotlin.Result<Unit> = runCatching {
        firestore.collection("staff")
            .document(staffId)
            .update(mapOf("active" to active))
            .await()
    }

    override suspend fun getStaffById(staffId: String): kotlin.Result<Staff?> = runCatching {
        val snapshot = firestore.collection("staff").document(staffId).get().await()
        snapshot.toObject(Staff::class.java)
    }

}

