package com.ikemba.inventrar.business.data.domain.useCase

import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.domain.model.AppUser
import com.ikemba.inventrar.business.data.domain.model.StaffRole


class AddStaffUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(user: AppUser, businessId: String, role: String, departmentId: String, branchId: String) =
        businessRepository.addStaffToBusiness(user, businessId, role, departmentId, branchId)
}