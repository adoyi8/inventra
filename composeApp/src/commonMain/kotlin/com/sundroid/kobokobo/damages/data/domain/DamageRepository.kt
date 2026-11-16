package com.sundroid.kobokobo.damages.data.domain

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import kotlinx.coroutines.flow.Flow

interface DamageRepository {
    suspend fun postDamage(damage: Damage): Result<Unit, DataError.Remote>
    fun getDamages(businessId: String): Flow<List<Damage>>
}