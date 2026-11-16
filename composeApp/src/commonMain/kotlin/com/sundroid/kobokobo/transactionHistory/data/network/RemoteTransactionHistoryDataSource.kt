package com.sundroid.kobokobo.transactionHistory.data.network

import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.heldOrder.data.dto.VoidOrderRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionHistoryDto
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest

interface RemoteTransactionHistoryDataSource {
    suspend fun getTransactionHistory(
        accessToken: String, paginationRequest: PaginationRequest
    ): Result<TransactionHistoryDto, DataError.Remote>


    suspend fun getHeldOrders(
        accessToken: String
    ): Result<TransactionHistoryDto, DataError.Remote>

    suspend fun holdOrder(
        voidOrderRequest: VoidOrderRequest
    ): Result<ResponseDto, DataError.Remote>

}