package com.sundroid.kobokobo.admin.data.domain.repository

import com.sundroid.kobokobo.admin.data.domain.model.Department
import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface DepartmentRepository {
    suspend fun createDepartment(department: Department): Result<Department, DataError>
    suspend fun updateDepartment(department: Department): Result<Department, DataError>
    suspend fun deleteDepartment(departmentId: String, businessId: String): EmptyResult<DataError>
    suspend fun getDepartmentById(departmentId: String, businessId: String): Result<Department?, DataError>
    fun getDepartments(businessId: String): Flow<List<Department>>
}




