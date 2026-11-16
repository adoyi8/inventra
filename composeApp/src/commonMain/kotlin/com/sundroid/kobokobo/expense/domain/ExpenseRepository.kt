package com.sundroid.kobokobo.expense.domain


import com.sundroid.kobokobo.transactionHistory.data.dto.PaginationRequest
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
interface ExpenseRepository {
    /**
     * Gets a paginated list of expenses based on filters.
     */
    suspend fun getExpenses(
        businessId: String,
        paginationRequest: PaginationRequest
    ): Result<ExpenseHistory, DataError.Remote>

    /**
     * Creates a new expense document in Firestore.
     */
    suspend fun createExpense(
        businessId: String,
        expenseDto: ExpenseDto
    ): Result<Unit, DataError.Remote>

    /**
     * Gets a distinct list of all categories used in expenses.
     */
    suspend fun getExpenseCategories(
        businessId: String
    ): Result<List<String>, DataError.Remote>
}