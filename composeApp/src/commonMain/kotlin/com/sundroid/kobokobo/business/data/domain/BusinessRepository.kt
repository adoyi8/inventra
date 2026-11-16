package com.sundroid.kobokobo.business.data.domain

import com.google.firebase.auth.FirebaseUser
import com.sundroid.kobokobo.business.data.database.Business
import com.sundroid.kobokobo.business.data.domain.model.AppUser
import com.sundroid.kobokobo.business.data.domain.model.PaymentMethod
import com.sundroid.kobokobo.business.data.domain.model.Staff
import com.sundroid.kobokobo.business.data.dto.CreateBusinessRequest
import com.sundroid.kobokobo.business.data.dto.OrganizationResponse
import com.sundroid.kobokobo.business.data.dto.SearchOrganizationRequest
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.SingleHeldOrderDto
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import kotlinx.coroutines.flow.Flow

interface BusinessRepository {


    suspend fun getSingleHeldOrder(accessToken: String, request: VoidOrderRequest): Result<SingleHeldOrderDto, DataError.Remote>
    suspend fun getBusinesses(accessToken: String, searchOrganizationRequest: SearchOrganizationRequest): Result<OrganizationResponse, DataError.Remote>
    suspend fun createBusiness(accessToken: String, createBusinessRequest: CreateBusinessRequest): Result<ResponseDto, DataError.Remote>
    suspend fun voidOrder(accessToken: String, voidOrderRequest: VoidOrderRequest): Result<ResponseDto, DataError.Remote>
    suspend fun getBusinessesForUser(userId: String): kotlin.Result<List<Business>>
    suspend fun createBusiness(businessName: String, address: String, features: List<String>, owner: FirebaseUser): kotlin.Result<Unit>
    suspend fun findUserByEmail(email: String): kotlin.Result<AppUser?>
    suspend fun addStaffToBusiness(user: AppUser, businessId: String, role: String, departmentId: String, branchId: String): kotlin.Result<Unit>
    suspend fun saveBusinessInLocalDb(business: Business): EmptyResult<DataError.Local>
    suspend fun getStaffRole(userId: String, businessId: String): kotlin.Result<Staff?>
    suspend fun getStaffById(staffId: String): kotlin.Result<Staff?>
    suspend fun setStaffActive(staffId: String, active: Boolean): kotlin.Result<Unit>
    suspend fun updateStaffRole(staffId: String, newRole: String, newRoleName:String): kotlin.Result<Unit>
    suspend fun getStaffForBusiness(businessId: String): kotlin.Result<List<Staff>>
    fun getBusinessFlow(businessId: String): Flow<Business?>
    suspend fun getPaymentMethods(businessId: String): kotlin.Result<List<PaymentMethod>>
    suspend fun addPaymentMethod(businessId: String, method: PaymentMethod): kotlin.Result<Unit>
    suspend fun updatePaymentMethod(businessId: String, method: PaymentMethod): kotlin.Result<Unit>
    suspend fun deletePaymentMethod(businessId: String, methodId: String): kotlin.Result<Unit>
}
