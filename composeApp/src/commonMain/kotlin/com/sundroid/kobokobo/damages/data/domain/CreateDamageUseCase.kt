package com.sundroid.kobokobo.damages.data.domain

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result


class CreateDamageUseCase(private val repository: DamageRepository) {
    suspend operator fun invoke(damage: Damage): Result<Unit, DataError.Remote> {
        // Add any business logic validation here (e.g., check if quantity > 0)
        if (damage.quantity <= 0) {
             // You might want a more specific error type
            return Result.Error(DataError.Remote.UNKNOWN) // Placeholder for validation error
        }
        return repository.postDamage(damage)
    }
}