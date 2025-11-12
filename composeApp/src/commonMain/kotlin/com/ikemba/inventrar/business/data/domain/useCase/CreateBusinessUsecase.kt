package com.ikemba.inventrar.business.data.domain.useCase

import com.google.firebase.auth.FirebaseUser
import com.ikemba.inventrar.business.data.domain.BusinessRepository
import com.ikemba.inventrar.business.data.domain.model.AppUser


class CreateBusinessUseCase(private val businessRepository: BusinessRepository) {
    suspend operator fun invoke(businessName: String, owner: FirebaseUser) =
        businessRepository.createBusiness(businessName, owner)
}