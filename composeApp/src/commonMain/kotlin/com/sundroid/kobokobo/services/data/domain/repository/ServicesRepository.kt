package com.sundroid.kobokobo.services.data.domain.repository

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.EmptyResult
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.services.data.domain.model.ServicesMaterial
import kotlinx.coroutines.flow.Flow

interface ServicesRepository {
    fun getServicesMaterials(businessId: String): Flow<List<ServicesMaterial>>
    suspend fun getServicesMaterialById(businessId: String, id: String): Result<ServicesMaterial, DataError>
    suspend fun addServicesMaterial(businessId: String, material: ServicesMaterial): EmptyResult<DataError>
    suspend fun updateServicesMaterial(businessId: String, material: ServicesMaterial): EmptyResult<DataError>
    suspend fun deleteServicesMaterial(businessId: String, id: String): EmptyResult<DataError>
}

