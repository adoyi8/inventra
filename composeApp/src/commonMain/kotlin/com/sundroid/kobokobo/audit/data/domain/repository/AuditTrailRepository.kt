package com.sundroid.kobokobo.audit.data.domain.repository

import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailDto
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrailPaginationRequest
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface AuditTrailRepository {
    suspend fun createAuditTrail(auditTrail: AuditTrail): Result<AuditTrail, DataError>
    suspend fun getAuditTrailsByProduct(productId: String, businessId: String): Flow<List<AuditTrail>>
    suspend fun getAuditTrailsByStaff(staffId: String, businessId: String): Flow<List<AuditTrail>>
    suspend fun getAuditTrailsByTransaction(transactionId: String, businessId: String): Flow<List<AuditTrail>>
    suspend fun getAuditTrails(
        businessId: String,
        paginationRequest: AuditTrailPaginationRequest
    ): Result<AuditTrailDto, DataError.Remote>
}









