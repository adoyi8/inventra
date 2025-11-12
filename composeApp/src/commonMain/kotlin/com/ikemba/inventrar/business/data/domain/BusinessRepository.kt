package com.ikemba.inventrar.business.data.domain

import com.google.firebase.auth.FirebaseUser
import com.ikemba.inventrar.business.data.database.Business
import com.ikemba.inventrar.business.data.domain.model.AppUser
import com.ikemba.inventrar.business.data.domain.model.Staff
import com.ikemba.inventrar.business.data.dto.CreateBusinessRequest
import com.ikemba.inventrar.business.data.dto.OrganizationResponse
import com.ikemba.inventrar.business.data.dto.SearchOrganizationRequest
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.EmptyResult
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.SingleHeldOrderDto
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest

interface BusinessRepository {


    suspend fun getSingleHeldOrder(accessToken: String, request: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun getBusinesses(accessToken: String, searchOrganizationRequest: SearchOrganizationRequest): Result<OrganizationResponse, DataError.Remote>
    suspend fun createBusiness(accessToken: String, createBusinessRequest: CreateBusinessRequest): Result<ResponseDto, DataError.Remote>
    suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote>
    suspend fun getBusinessesForUser(userId: String): kotlin.Result<List<Business>>
    suspend fun createBusiness(businessName: String, owner: FirebaseUser): kotlin.Result<Unit>
    suspend fun findUserByEmail(email: String): kotlin.Result<AppUser?>
    suspend fun addStaffToBusiness(user: AppUser, businessId: String, role: String, departmentId: String, branchId: String): kotlin.Result<Unit>
    suspend fun saveBusinessInLocalDb(business: Business): EmptyResult<DataError.Local>
    suspend fun getStaffRole(userId: String, businessId: String): kotlin.Result<Staff?>
    suspend fun getStaffById(staffId: String): kotlin.Result<Staff?>
    suspend fun setStaffActive(staffId: String, active: Boolean): kotlin.Result<Unit>
    suspend fun updateStaffRole(staffId: String, newRole: String): kotlin.Result<Unit>
    suspend fun getStaffForBusiness(businessId: String): kotlin.Result<List<Staff>>
}
