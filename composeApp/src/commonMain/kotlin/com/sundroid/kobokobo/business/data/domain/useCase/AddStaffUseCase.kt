package com.sundroid.kobokobo.business.data.domain.useCase

import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.AppUser
import com.sundroid.kobokobo.business.data.domain.model.StaffRole


class AddStaffUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(user: AppUser, businessId: String, role: String, departmentId: String, branchId: String) =
        businessRepository.addStaffToBusiness(user, businessId, role, departmentId, branchId)
}