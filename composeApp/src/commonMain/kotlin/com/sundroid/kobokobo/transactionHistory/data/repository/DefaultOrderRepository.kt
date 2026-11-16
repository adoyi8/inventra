package com.sundroid.kobokobo.transactionHistory.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.core.data.dto.ResponseDto
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.transactionHistory.data.dto.Transaction
import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionData
import com.sundroid.kobokobo.transactionHistory.data.dto.TransactionHistoryDto
import com.sundroid.kobokobo.transactionHistory.domain.TransactionHistoryRepository
import com.sundroid.kobokobo.utils.StringUtil.BUSINESS_COLLECTION
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DefaultTransactionHistoryRepository(
    private val firestore: FirebaseFirestore,
): TransactionHistoryRepository {




    override suspend fun getTransactionHistory(businessId: String, paginationRequest: PaginationRequest): Result<TransactionHistoryDto, DataError.Remote> {
        println("angel 1 - Applying server-side filters")
        return try {
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)

            // 1. Start with the base query
            var query: Query = busRef.collection("sales")
                .whereEqualTo("is_held", false)

            // --- 2. APPLY ALL FILTERS DYNAMICALLY ---

            // Keyword Filter (only applies if set)
            paginationRequest.keyword?.takeIf { it.isNotBlank() }?.let {
                // This is a basic filter. For real search, you need a 3rd party service.
                query = query.whereEqualTo("orderId", it)
            }

            // Staff Filter (only applies if set)
            paginationRequest.staff_user_id?.let { userId ->
                println("onyeka 2 "+ userId)
                query = query.whereEqualTo("createdBy", userId)
            }

            // --- 👇 NEW PAYMENT METHOD FILTER 👇 ---
            paginationRequest.payment_method?.let { method ->
                query = query.whereEqualTo("payment_method", method)
            }

            // Period Filter (e.g., "daily", "weekly")
            // This is only applied if a date range is NOT set.
            if (paginationRequest.start_date.isNullOrBlank()) {
                paginationRequest.filter_by?.let {
                    val (start, end) = getStartAndEndTimestamps(it)
                    start?.let { query = query.whereGreaterThanOrEqualTo("createdAt", it) }
                    end?.let { query = query.whereLessThan("createdAt", it) }
                }
            }

            // Date Range Filter (overrides period filter)
            paginationRequest.start_date?.takeIf { it.isNotBlank() }?.let { dateStr ->
                val formatStr = if (dateStr.length > 10) "yyyy-MM-dd HH:mm" else "yyyy-MM-dd"
                val startTimestamp = SimpleDateFormat(formatStr, Locale.getDefault()).parse(dateStr)
                if (startTimestamp != null) {
                    query = query.whereGreaterThanOrEqualTo("createdAt", Timestamp(startTimestamp))
                }
            }
            paginationRequest.end_date?.takeIf { it.isNotBlank() }?.let { dateStr ->
                val formatStr = if (dateStr.length > 10) "yyyy-MM-dd HH:mm" else "yyyy-MM-dd"
                val endTimestamp = SimpleDateFormat(formatStr, Locale.getDefault()).parse(dateStr)
                if (endTimestamp != null) {
                    val queryTime = if (dateStr.length > 10) {
                        endTimestamp
                    } else {
                        // Go to the end of the selected day if no specific time is provided
                        Calendar.getInstance().apply {
                            time = endTimestamp
                            add(Calendar.DAY_OF_YEAR, 1)
                        }.time
                    }
                    query = query.whereLessThan("createdAt", Timestamp(queryTime))
                }
            }

            // --- 3. PAGINATION (SERVER-SIDE) ---

            // 3a. Get the TOTAL count of filtered documents
            val countSnapshot = query.count().get(AggregateSource.SERVER).await()
            val total = countSnapshot.count.toInt()
            val totalPages = if (paginationRequest.limit == 0) 1 else ((total + paginationRequest.limit - 1) / paginationRequest.limit)
            val page = paginationRequest.page.toInt()

            // 3b. Build the final paginated query
            var paginatedQuery = query.orderBy("createdAt", Query.Direction.DESCENDING)

            if (page > 1 && total > 0) {
                val offset = (page - 1) * paginationRequest.limit
                val prevPageSnapshot = query.orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(offset.toLong())
                    .get().await()
                if (prevPageSnapshot.documents.isNotEmpty()) {
                    val lastDoc = prevPageSnapshot.documents.last()
                    paginatedQuery = paginatedQuery.startAfter(lastDoc)
                }
            }

            paginatedQuery = paginatedQuery.limit(paginationRequest.limit.toLong())

            // 4. EXECUTE QUERY and MAP RESULTS
            val snapshot = paginatedQuery.get().await()
            val history = snapshot.documents.map { doc ->
                Transaction(
                    orderId = doc.id,
                    created = (doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L).toString(),
                    paymentMethod = doc.getString("payment_method") ?: "",
                    totalAmount = ((doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0).toString(),
                    createdBy = doc.getString("created_by") ?: "",
                    paymentMethodText = doc.getString("payment_method_text"),
                    isPaid = doc.getBoolean("is_paid") ?: true,
                    paymentProcessedBy = doc.getString("payment_processed_by")
                )
            }

            println("angel 2, fetched ${history.size} of $total documents")

            Result.Success(
                TransactionHistoryDto(
                    responseCode = 0,
                    responseMessage = "OK",
                    data = TransactionData(
                        history = history,
                        page = page,
                        limit = paginationRequest.limit,
                        totalOrders = total,
                        totalPages = totalPages
                    )
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }


    // Helper for daily/weekly/monthly filters
    fun getStartAndEndTimestamps(filter: String?): Pair<Timestamp?, Timestamp?> {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)

        val start: Date
        val end: Date

        when (filter) {
            "daily" -> {
                start = cal.time
                cal.add(Calendar.DAY_OF_YEAR, 1)
                end = cal.time
            }
            "weekly" -> {
                cal.set(Calendar.DAY_OF_WEEK, cal.firstDayOfWeek)
                start = cal.time
                cal.add(Calendar.WEEK_OF_YEAR, 1)
                end = cal.time
            }
            "monthly" -> {
                cal.set(Calendar.DAY_OF_MONTH, 1)
                start = cal.time
                cal.add(Calendar.MONTH, 1)
                end = cal.time
            }
            else -> return Pair(null, null)
        }
        return Pair(Timestamp(start), Timestamp(end))
    }
    override suspend fun getHeldOrders(accessToken: String): Result<TransactionHistoryDto, DataError.Remote> {
        return try {
            val snapshot = firestore.collection("held_orders").get().await()
            val history = snapshot.documents.map { doc ->
                Transaction(
                    orderId = doc.id,
                    created = (doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L).toString(),
                    paymentMethod = doc.getString("payment_method") ?: "cash",
                    totalAmount = ((doc.get("grandTotal") as? Number)?.toDouble() ?: 0.0).toString(),
                    createdBy = doc.getString("createdBy") ?: "",
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
