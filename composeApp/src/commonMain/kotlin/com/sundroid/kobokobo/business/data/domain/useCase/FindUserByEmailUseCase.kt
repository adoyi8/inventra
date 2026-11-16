package com.sundroid.kobokobo.business.data.domain.useCase

import com.sundroid.kobokobo.business.data.domain.BusinessRepository


class FindUserByEmailUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(email: String) = businessRepository.findUserByEmail(email)
}