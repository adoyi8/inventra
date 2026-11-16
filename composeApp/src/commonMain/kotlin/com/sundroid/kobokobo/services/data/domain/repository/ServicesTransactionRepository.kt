package com.sundroid.kobokobo.services.data.domain.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.services.data.domain.model.ServicesTransaction
import kotlinx.coroutines.flow.Flow

interface ServicesTransactionRepository {
    suspend fun postServicesTransaction(businessId: String, transaction: ServicesTransaction): Result<ServicesTransaction, DataError>
    suspend fun markTransactionPaid(businessId: String, transactionId: String, paymentMethod: String, processedBy: String): Result<Unit, DataError>
    fun getServicesTransactions(businessId: String): Flow<List<ServicesTransaction>>
}
