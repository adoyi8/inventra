package com.ikemba.inventrar.transactionHistory.data.dto

import kotlinx.serialization.Serializable

@Serializable
data class PaginationRequest(
    var keyword: String? = null,
    var start_date: String? = null,
    var end_date: String? = null,
    var filter_by: String? = null,
    var page: Long = 1,
    var limit:Int = 10
)