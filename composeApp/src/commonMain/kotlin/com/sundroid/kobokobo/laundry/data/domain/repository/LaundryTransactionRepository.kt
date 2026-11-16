package com.sundroid.kobokobo.laundry.data.domain.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryTransaction
import kotlinx.coroutines.flow.Flow

interface LaundryTransactionRepository {
    suspend fun postLaundryTransaction(businessId: String, transaction: LaundryTransaction): Result<LaundryTransaction, DataError>
    fun getLaundryTransactions(businessId: String): Flow<List<LaundryTransaction>>
}
