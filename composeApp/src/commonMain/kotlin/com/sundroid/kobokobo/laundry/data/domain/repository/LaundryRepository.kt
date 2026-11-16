package com.sundroid.kobokobo.laundry.data.domain.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.laundry.data.domain.model.LaundryMaterial
import kotlinx.coroutines.flow.Flow

interface LaundryRepository {
    fun getLaundryMaterials(businessId: String): Flow<List<LaundryMaterial>>
    suspend fun getLaundryMaterialById(businessId: String, id: String): Result<LaundryMaterial, DataError>
    suspend fun addLaundryMaterial(businessId: String, material: LaundryMaterial): EmptyResult<DataError>
    suspend fun updateLaundryMaterial(businessId: String, material: LaundryMaterial): EmptyResult<DataError>
    suspend fun deleteLaundryMaterial(businessId: String, id: String): EmptyResult<DataError>
}
