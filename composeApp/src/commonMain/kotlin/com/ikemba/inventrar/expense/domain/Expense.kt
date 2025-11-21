package com.ikemba.inventrar.expense.domain

import com.google.firebase.firestore.ServerTimestamp
import java.util.Date

// The domain model for an expense
data class Expense(
    val expenseId: String = "",
    val description: String = "",
    val amount: Double = 0.0,
    val created: Long = 0L,
    val createdBy: String = "", // Staff User ID
    val paymentMethod: String = "",
    val category: String = ""
)

// The model for Firestore (allows using @ServerTimestamp)
data class ExpenseDto(
    val description: String = "",
    val amount: Double = 0.0,
    @ServerTimestamp val createdAt: Date? = null,
    val createdBy: String = "",
    val paymentMethod: String = "",
    val category: String = ""
)

// A wrapper for paginated expense results, just like TransactionHistory
data class ExpenseHistory(
    val expenses: List<Expense> = emptyList(),
    val totalOrders: Int = 0,
    val totalPages: Int = 1,
    val limit: Int = 10,
    val page: Int = 1
)