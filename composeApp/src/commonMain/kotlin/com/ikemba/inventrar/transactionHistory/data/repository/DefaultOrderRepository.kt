package com.ikemba.inventrar.transactionHistory.data.repository

import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.transactionHistory.data.dto.TransactionHistoryDto
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.transactionHistory.data.network.RemoteTransactionHistoryDataSource
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistoryRepository

class DefaultTransactionHistoryRepository(
    private val remoteTransactionHistoryDataSource: RemoteTransactionHistoryDataSource,
): TransactionHistoryRepository {

    override suspend fun getTransactionHistory(accessToken: String, paginationRequest: PaginationRequest): Result<TransactionHistoryDto, DataError.Remote> {
        return remoteTransactionHistoryDataSource
            .getTransactionHistory(accessToken, paginationRequest)

            }

    override suspend fun getHeldOrders(accessToken: String): Result<TransactionHistoryDto, DataError.Remote> {
        TODO("Not yet implemented")
    }

    override suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote> {
        TODO("Not yet implemented")
    }
}

