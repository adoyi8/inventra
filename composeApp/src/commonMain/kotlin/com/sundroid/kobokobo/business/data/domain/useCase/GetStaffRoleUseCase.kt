package com.sundroid.kobokobo.business.data.domain.useCase

import com.sundroid.kobokobo.business.data.domain.BusinessRepository

class GetStaffRoleUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(userId: String, businessId: String) =
        businessRepository.getStaffRole(userId, businessId)
}