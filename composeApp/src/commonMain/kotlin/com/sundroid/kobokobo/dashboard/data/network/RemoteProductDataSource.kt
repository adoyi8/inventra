package com.sundroid.kobokobo.dashboard.data.network

import com.sundroid.kobokobo.core.domain.DataError
import com.sundroid.kobokobo.core.domain.Result
import com.sundroid.kobokobo.dashboard.data.dto.ProductResponseDto

interface RemoteProductDataSource {
    suspend fun getProducts(
        accessToken: String
    ): Result<ProductResponseDto, DataError.Remote>

}