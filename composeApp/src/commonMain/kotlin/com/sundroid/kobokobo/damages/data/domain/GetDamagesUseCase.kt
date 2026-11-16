package com.sundroid.kobokobo.damages.data.domain

import kotlinx.coroutines.flow.Flow

class GetDamagesUseCase(private val repository: DamageRepository) {
    operator fun invoke(businessId: String): Flow<List<Damage>> {
        return repository.getDamages(businessId)
    }
}