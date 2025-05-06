package com.ikemba.inventrar.transactionHistory.data.network

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.heldOrder.data.dto.VoidOrderRequest
import com.ikemba.inventrar.transactionHistory.data.dto.TransactionHistoryDto
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

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