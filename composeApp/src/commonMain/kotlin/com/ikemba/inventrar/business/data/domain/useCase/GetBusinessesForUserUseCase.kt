package com.ikemba.inventrar.business.data.domain.useCase

import com.ikemba.inventrar.business.data.domain.BusinessRepository


class GetBusinessesForUserUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(userId: String) = businessRepository.getBusinessesForUser(userId)
}