package com.ikemba.inventrar.transactionHistory.domain

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.transactionHistory.data.dto.TransactionHistoryDto
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

interface TransactionHistoryRepository {

    suspend fun getTransactionHistory(accessToken: String, paginationRequest: PaginationRequest): Result<TransactionHistoryDto, DataError.Remote>
    suspend fun getHeldOrders(accessToken: String): Result<TransactionHistoryDto, DataError.Remote>
    suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote>

}