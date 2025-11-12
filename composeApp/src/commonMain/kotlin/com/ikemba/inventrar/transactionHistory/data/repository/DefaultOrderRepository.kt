package com.ikemba.inventrar.transactionHistory.data.repository

import com.google.firebase.firestore.FirebaseFirestore
import com.ikemba.inventrar.core.data.dto.ResponseDto
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.transactionHistory.data.dto.Transaction
import com.ikemba.inventrar.transactionHistory.data.dto.TransactionData
import com.ikemba.inventrar.transactionHistory.data.dto.TransactionHistoryDto
import com.ikemba.inventrar.transactionHistory.domain.TransactionHistoryRepository
import kotlinx.coroutines.tasks.await

class DefaultTransactionHistoryRepository(
    private val firestore: FirebaseFirestore,
): TransactionHistoryRepository {

    override suspend fun getTransactionHistory(accessToken: String, paginationRequest: PaginationRequest): Result<TransactionHistoryDto, DataError.Remote> {
        println("angel 1")
        return try {
            var query = firestore.collection("sales").whereEqualTo("is_held", false)
            // Optional keyword or date filters can be applied here if fields exist

            val snapshot = query.get().await()
            val history = snapshot.documents.map { doc ->
                Transaction(
                    orderId = doc.id,
                    created = (doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L).toString(),
                    paymentMethod = doc.getString("payment_method") ?: "cash",
                    totalAmount = ((doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0).toString()
                )
            }
            val page = paginationRequest.page.toInt()
            val limit = paginationRequest.limit
            val total = history.size
            println("angel 2 "+ total)
            val totalPages = if (limit == 0) 1 else ((total + limit - 1) / limit)
            val paged = if (limit > 0) history.drop((page - 1) * limit).take(limit) else history
            Result.Success(
                TransactionHistoryDto(
                    responseCode = 0,
                    responseMessage = "OK",
                    data = TransactionData(
                        history = paged,
                        page = page,
                        limit = limit,
                        totalOrders = total,
                        totalPages = totalPages
                    )
                )
            )
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getHeldOrders(accessToken: String): Result<TransactionHistoryDto, DataError.Remote> {
        return try {
            val snapshot = firestore.collection("held_orders").get().await()
            val history = snapshot.documents.map { doc ->
                Transaction(
                    orderId = doc.id,
                    created = (doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L).toString(),
                    paymentMethod = doc.getString("payment_method") ?: "cash",
                    totalAmount = ((doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0).toString()
                )
            }
            Result.Success(
                TransactionHistoryDto(
                    responseCode = 0,
                    responseMessage = "OK",
                    data = TransactionData(
                        history = history,
                        page = 1,
                        limit = history.size,
                        totalOrders = history.size,
                        totalPages = 1
                    )
                )
            )
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun holdOrder(accessToken: String): Result<ResponseDto, DataError.Remote> {
        return Result.Success(ResponseDto(responseCode = 0, responseMessage = "OK"))
    }
}

