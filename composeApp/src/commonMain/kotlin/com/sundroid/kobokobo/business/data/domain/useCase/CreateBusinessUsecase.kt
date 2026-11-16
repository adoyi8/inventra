package com.sundroid.kobokobo.business.data.domain.useCase

import com.google.firebase.auth.FirebaseUser
import com.sundroid.kobokobo.business.data.domain.BusinessRepository
import com.sundroid.kobokobo.business.data.domain.model.AppUser


class CreateBusinessUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(businessName: String, address: String, features: List<String>, owner: FirebaseUser) =
        businessRepository.createBusiness(businessName, address, features, owner)
}