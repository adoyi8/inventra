package com.sundroid.kobokobo.admin.data.domain.useCase

import com.sundroid.kobokobo.admin.data.domain.model.Department
import com.sundroid.kobokobo.admin.data.domain.repository.DepartmentRepository
import kotlinx.coroutines.flow.Flow

class CreateDepartmentUseCase(private val repo: DepartmentRepository) {
    suspend operator fun invoke(department: Department) = repo.createDepartment(department)
}

class UpdateDepartmentUseCase(private val repo: DepartmentRepository) {
    suspend operator fun invoke(department: Department) = repo.updateDepartment(department)
}

class DeleteDepartmentUseCase(private val repo: DepartmentRepository) {
    suspend operator fun invoke(departmentId: String, businessId: String) = repo.deleteDepartment(departmentId, businessId)
}

class GetDepartmentByIdUseCase(private val repo: DepartmentRepository) {
    suspend operator fun invoke(departmentId: String, businessId: String) = repo.getDepartmentById(departmentId, businessId)
}

class GetDepartmentsUseCase(private val repo: DepartmentRepository) {
    operator fun invoke(businessId: String): Flow<List<Department>> = repo.getDepartments(businessId)
}




