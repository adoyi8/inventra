package com.sundroid.kobokobo.transactionHistory.domain

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionHistoryDto
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest

interface TransactionHistoryRepository {

    suspend fun getTransactionHistory(businessId: String, paginationRequest: PaginationRequest): Result<TransactionHistoryDto, DataError.Remote>
    suspend fun getHeldOrders(accessToken: String): Result<TransactionHistoryDto, DataError.Remote>
    suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote>

}