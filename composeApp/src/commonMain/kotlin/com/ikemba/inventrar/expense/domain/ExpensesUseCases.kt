package com.ikemba.inventrar.expense.domain

import com.ikemba.inventrar.transactionHistory.data.dto.PaginationRequest

// GetExpensesUseCase.kt
class GetExpensesUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        businessId: String,
        paginationRequest: PaginationRequest
    ) = repository.getExpenses(businessId, paginationRequest)
}

// CreateExpenseUseCase.kt
class CreateExpenseUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        businessId: String,
        expenseDto: ExpenseDto
    ) = repository.createExpense(businessId, expenseDto)
}

// GetExpenseCategoriesUseCase.kt
class GetExpenseCategoriesUseCase(
    private val repository: ExpenseRepository
) {
    suspend operator fun invoke(
        businessId: String
    ) = repository.getExpenseCategories(businessId)
}