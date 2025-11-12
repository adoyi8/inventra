package com.ikemba.inventrar.business.data.domain.useCase

import com.ikemba.inventrar.business.data.domain.BusinessRepository

class GetStaffRoleUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(userId: String, businessId: String) =
        businessRepository.getStaffRole(userId, businessId)
}