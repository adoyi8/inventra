package com.sundroid.kobokobo.audit.data.domain.useCase

import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailDto
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailPaginationRequest
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

class CreateAuditTrailUseCase(private val repo: AuditTrailRepository) {
    suspend operator fun invoke(auditTrail: AuditTrail) = repo.createAuditTrail(auditTrail)
}

class GetAuditTrailsUseCase(private val repo: AuditTrailRepository) {
    suspend operator fun invoke(businessId: String, paginationRequest: AuditTrailPaginationRequest): Result<AuditTrailDto, DataError.Remote> = repo.getAuditTrails(businessId, paginationRequest)
}

class GetAuditTrailsByProductUseCase(private val repo: AuditTrailRepository) {
    suspend operator fun invoke(productId: String, businessId: String): Flow<List<AuditTrail>> = repo.getAuditTrailsByProduct(productId, businessId)
}

class GetAuditTrailsByStaffUseCase(private val repo: AuditTrailRepository) {
    suspend operator fun invoke(staffId: String, businessId: String): Flow<List<AuditTrail>> = repo.getAuditTrailsByStaff(staffId, businessId)
}

class GetAuditTrailsByTransactionUseCase(private val repo: AuditTrailRepository) {
    suspend operator fun invoke(transactionId: String, businessId: String): Flow<List<AuditTrail>> = repo.getAuditTrailsByTransaction(transactionId, businessId)
}









