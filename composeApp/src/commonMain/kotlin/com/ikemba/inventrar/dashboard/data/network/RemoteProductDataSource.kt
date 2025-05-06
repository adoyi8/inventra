package com.ikemba.inventrar.dashboard.data.network

import com.ikemba.inventrar.core.domain.DataError
import com.ikemba.inventrar.core.domain.Result
import com.ikemba.inventrar.dashboard.data.dto.ProductResponseDto

interface RemoteProductDataSource {
    suspend fun getProducts(
        accessToken: String
    ): Result<ProductResponseDto, DataError.Remote>

}