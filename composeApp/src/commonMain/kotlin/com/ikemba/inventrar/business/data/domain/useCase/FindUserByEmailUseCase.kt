package com.ikemba.inventrar.business.data.domain.useCase

import com.ikemba.inventrar.business.data.domain.BusinessRepository


class FindUserByEmailUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(email: String) = businessRepository.findUserByEmail(email)
}