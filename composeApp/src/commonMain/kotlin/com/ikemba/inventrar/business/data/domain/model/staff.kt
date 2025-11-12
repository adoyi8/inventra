package com.ikemba.inventrar.business.data.domain.model

enum class StaffRole {
    MANAGER, EMPLOYEE
}

data class Staff(
    val staffId: String = "",
    val userId: String = "",
    val businessId: String = "",
    val departmentId: String = "",
    val branchId: String = "",
    val role: String = "",
    val active: Boolean = true
)