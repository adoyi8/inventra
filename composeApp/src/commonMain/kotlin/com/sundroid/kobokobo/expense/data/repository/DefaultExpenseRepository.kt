package com.sundroid.kobokobo.expense.data.repository

import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.AggregateSource
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.sundroid.kobokobo.audit.data.domain.model.ActionType
import com.sundroid.kobokobo.audit.data.domain.model.AuditTrail
import com.sundroid.kobokobo.audit.data.domain.model.EntityType
import com.sundroid.kobokobo.audit.data.domain.repository.AuditTrailRepository
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.utils.Util.currencyFormat
import com.sundroid.kobokobo.expense.domain.Expense
import com.sundroid.kobokobo.expense.domain.ExpenseDto
import com.sundroid.kobokobo.expense.domain.ExpenseHistory
import com.sundroid.kobokobo.expense.domain.ExpenseRepository
import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import kotlinx.coroutines.tasks.await
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class DefaultExpenseRepository(
    private val firestore: FirebaseFirestore,
    private val auditTrailRepository: AuditTrailRepository
) : ExpenseRepository {

    private val BUSINESS_COLLECTION = "businesses" // Or your constant
    private val EXPENSE_COLLECTION = "expenses"

    override suspend fun getExpenses(
        businessId: String,
        paginationRequest: PaginationRequest
    ): Result<ExpenseHistory, DataError.Remote> {
        return try {
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)

            // 1. Base query
            var query: Query = busRef.collection(EXPENSE_COLLECTION)

            // 2. Apply all filters
            paginationRequest.keyword?.takeIf { it.isNotBlank() }?.let {
                // Note: Firestore search is limited. This is a basic "starts with" query.
                // For "contains", you'd need a 3rd party like Algolia.
                query = query.orderBy("description")
                    .startAt(it)
                    .endAt(it + '\uf8ff')
            }

            paginationRequest.staff_user_id?.let {
                query = query.whereEqualTo("createdBy", it)
            }

            paginationRequest.payment_method?.let {
                query = query.whereEqualTo("paymentMethod", it)
            }

            paginationRequest.category?.let {
                query = query.whereEqualTo("category", it)
            }

            // Date Range (if set, overrides 'filter_by')
            if (!paginationRequest.start_date.isNullOrBlank()) {
                paginationRequest.start_date?.let {
                    val startTimestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                    if (startTimestamp != null) {
                        query = query.whereGreaterThanOrEqualTo("createdAt", Timestamp(startTimestamp))
                    }
                }
                paginationRequest.end_date?.let {
                    val endTimestamp = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).parse(it)
                    if (endTimestamp != null) {
                        val endOfDay = Calendar.getInstance().apply {
                            time = endTimestamp
                            add(Calendar.DAY_OF_YEAR, 1)
                        }.time
                        query = query.whereLessThan("createdAt", Timestamp(endOfDay))
                    }
                }
            } else {
                // Period Filter (daily, weekly, etc.)
                paginationRequest.filter_by?.let {
                    val (start, end) = getStartAndEndTimestamps(it) // Use helper from your Txn repo
                    start?.let { query = query.whereGreaterThanOrEqualTo("createdAt", it) }
                    end?.let { query = query.whereLessThan("createdAt", it) }
                }
            }

            // 3. Pagination - Get Total Count
            val countSnapshot = query.count().get(AggregateSource.SERVER).await()
            val total = countSnapshot.count.toInt()
            val totalPages = if (paginationRequest.limit == 0) 1 else ((total + paginationRequest.limit - 1) / paginationRequest.limit)
            val page = paginationRequest.page.toInt()

            // 4. Build final paginated query
            var paginatedQuery = query.orderBy("createdAt", Query.Direction.DESCENDING)

            if (page > 1 && total > 0) {
                val offset = (page - 1) * paginationRequest.limit
                val prevPageSnapshot = query.orderBy("createdAt", Query.Direction.DESCENDING)
                    .limit(offset.toLong())
                    .get().await()
                if (prevPageSnapshot.documents.isNotEmpty()) {
                    paginatedQuery = paginatedQuery.startAfter(prevPageSnapshot.documents.last())
                }
            }
            paginatedQuery = paginatedQuery.limit(paginationRequest.limit.toLong())

            // 5. Execute and map
            val snapshot = paginatedQuery.get().await()
            val expenses = snapshot.documents.map { doc ->
                Expense(
                    expenseId = doc.id,
                    description = doc.getString("description") ?: "",
                    amount = (doc.get("amount") as? Number)?.toDouble() ?: 0.0,
                    created = doc.getTimestamp("createdAt")?.toDate()?.time ?: 0L,
                    createdBy = doc.getString("createdBy") ?: "",
                    paymentMethod = doc.getString("paymentMethod") ?: "",
                    category = doc.getString("category") ?: ""
                )
            }

            Result.Success(
                ExpenseHistory(
                    expenses = expenses,
                    page = page,
                    limit = paginationRequest.limit,
                    totalOrders = total,
                    totalPages = totalPages
                )
            )
        } catch (e: Exception) {
            e.printStackTrace()
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun createExpense(
        businessId: String,
        expenseDto: ExpenseDto
    ): Result<Unit, DataError.Remote> {
        return try {
            val busRef = firestore.collection(BUSINESS_COLLECTION).document(businessId)
            val docRef = busRef.collection(EXPENSE_COLLECTION).add(expenseDto).await()

            val auditTrail = AuditTrail(
                staffId = expenseDto.createdBy,
                businessId = businessId,
                auditTrailMessage = "${FirebaseAuth.getInstance().currentUser?.displayName} Created new expense: ${expenseDto.description} with amount ${currencyFormat(expenseDto.amount)}",
                actionType = ActionType.CREATE,
                entityType = EntityType.TRANSACTION,
                entityId = docRef.id,
                newValues = mapOf(
                    "description" to expenseDto.description,
                    "amount" to expenseDto.amount,
                    "category" to expenseDto.category,
                    "paymentMethod" to expenseDto.paymentMethod
                )
            )
            auditTrailRepository.createAuditTrail(auditTrail)
            Result.Success(Unit)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

    override suspend fun getExpenseCategories(
        businessId: String
    ): Result<List<String>, DataError.Remote> {
        return try {
            val snapshot = firestore.collection(BUSINESS_COLLECTION).document(businessId)
                .collection(EXPENSE_COLLECTION)
                .get().await()

            val categories = snapshot.documents
                .mapNotNull { it.getString("category") }
                .filter { it.isNotBlank() }
                .distinct()
                .sorted()

            Result.Success(categories)
        } catch (e: Exception) {
            Result.Error(DataError.Remote.UNKNOWN)
        }
    }

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
}