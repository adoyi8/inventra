package com.ikemba.inventrar.expense.domain


import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest
import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
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